package com.xzakota.gradle.plugin.xposed

import com.joom.grip.Grip
import com.joom.grip.GripFactory
import com.joom.grip.io.FileSource
import com.joom.grip.io.IoFactory
import com.joom.grip.mirrors.getObjectType
import com.joom.grip.mirrors.getObjectTypeByInternalName
import com.xzakota.extension.createDirectory
import com.xzakota.extension.createFile
import com.xzakota.extension.safeMkdirs
import com.xzakota.xposed.annotation.LSPosedModule
import com.xzakota.xposed.annotation.XposedModule
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.objectweb.asm.Opcodes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarOutputStream

@Suppress("unused")
abstract class GenerateModuleEntryTask : BaseModuleGenerateTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val allJars : ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val allDirectories : ListProperty<Directory>

    @get:OutputFile
    abstract val classOutput : RegularFileProperty

    @get:OutputDirectory
    abstract val entryOutput : DirectoryProperty

    init {
        description = "Generate module entry class"
    }

    override fun onAction() {
        val inputs = (allJars.get() + allDirectories.get()).map { it.asFile.toPath() }
        val grip = GripFactory.newInstance(Opcodes.ASM9).create(inputs)

        JarOutputStream(BufferedOutputStream(FileOutputStream(classOutput.get().asFile))).use { jarOutput ->
            val sources = inputs.asSequence().map { input ->
                IoFactory.createFileSource(input)
            }

            try {
                sources.forEach { source ->
                    source.listFiles { name, type ->
                        when (type) {
                            FileSource.EntryType.CLASS -> processClass(grip, source, jarOutput, name)
                            FileSource.EntryType.FILE -> jarOutput.createFile(name, source.readFile(name))
                            FileSource.EntryType.DIRECTORY -> jarOutput.createDirectory(name)
                        }
                    }
                }
            } finally {
                sources.forEach {
                    it.close()
                }
            }
        }

        if (DataProvider.isSupportXposed) {
            val xposedPropertiesDir = File(entryOutput.get().asFile, "assets")
            xposedPropertiesDir.safeMkdirs()
            File(xposedPropertiesDir, "xposed_init").writeText(DataProvider.xposedEntryClassName)
        }

        if (DataProvider.isSupportLSPosed) {
            val xposedPropertiesDir = File(entryOutput.get().asFile, "META-INF/xposed")
            xposedPropertiesDir.safeMkdirs()
            File(xposedPropertiesDir, "java_init.list").writeText(DataProvider.lsposedEntryClassName)
        }
    }

    private fun processClass(grip : Grip, source : FileSource, jarOutput : JarOutputStream, name : String) {
        if (!name.endsWith(".class")) {
            return
        }

        val pathName = name.substringBeforeLast(".class").replace('\\', '/')
        val type = getObjectTypeByInternalName(pathName)
        val annotations = grip.classRegistry.getClassMirror(type).annotations

        if (ANNOTATION_XPOSED in annotations) {
            DataProvider.xposedEntryClassName = pathName.replace("/", ".")
        } else if (ANNOTATION_LSPOSED in annotations) {
            DataProvider.lsposedEntryClassName = pathName.replace("/", ".")
        }

        jarOutput.createFile(name, source.readFile(name))
    }

    private companion object {
        val ANNOTATION_XPOSED = getObjectType(XposedModule::class.java)
        val ANNOTATION_LSPOSED = getObjectType(LSPosedModule::class.java)
    }
}
