package com.xzakota.gradle.plugin.xposed

import com.joom.grip.ClassRegistry
import com.joom.grip.GripFactory
import com.joom.grip.io.FileSource
import com.joom.grip.io.IoFactory
import com.joom.grip.mirrors.ClassMirror
import com.joom.grip.mirrors.getObjectType
import com.joom.grip.mirrors.getObjectTypeByInternalName
import com.xzakota.extension.createDirectory
import com.xzakota.extension.createFile
import com.xzakota.extension.safeMkdirs
import com.xzakota.xposed.annotation.ModuleEntry
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
        description = "Generate module entry file"
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
                            FileSource.EntryType.CLASS -> processClass(grip.classRegistry, source, jarOutput, name)
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

        if (DataProvider.isSupportXposed()) {
            val assetsDir = File(entryOutput.get().asFile, "assets")
            assetsDir.safeMkdirs()
            File(assetsDir, "xposed_init").writeText(DataProvider.xposedEntryClassName)
        }

        if (DataProvider.isSupportLSPosed()) {
            val modulePropertiesDir = File(entryOutput.get().asFile, "META-INF/xposed")
            modulePropertiesDir.safeMkdirs()
            File(modulePropertiesDir, "java_init.list").writeText(DataProvider.lsposedEntryClassName)
        }
    }

    private fun processClass(classRegistry : ClassRegistry, source : FileSource, jarOutput : JarOutputStream, name : String) {
        if (!name.endsWith(".class")) {
            return
        }

        val pathName = name.substringBeforeLast(".class").replace('\\', '/')
        val type = getObjectTypeByInternalName(pathName)
        val classMirror = classRegistry.getClassMirror(type)

        if (ANNOTATION_XPOSED_MODULE_ENTRY in classMirror.annotations) {
            if (isLSPosedModuleEntry(classRegistry, classMirror)) {
                DataProvider.lsposedEntryClassName = pathName.replace("/", ".")
            } else {
                DataProvider.xposedEntryClassName = pathName.replace("/", ".")
            }
        }

        jarOutput.createFile(name, source.readFile(name))
    }

    private fun isLSPosedModuleEntry(classRegistry : ClassRegistry, classMirror : ClassMirror) : Boolean {
        var type = classMirror.superType
        while (type != null && type != TYPE_ANY) {
            if (type == TYPE_XPOSED_MODULE) {
                return true
            }

            type = classRegistry.getClassMirror(type).superType
        }

        return false
    }

    private companion object {
        val TYPE_ANY = getObjectType(Any::class.java)
        val TYPE_XPOSED_MODULE = getObjectType("Lio/github/libxposed/api/XposedModule;")
        val ANNOTATION_XPOSED_MODULE_ENTRY = getObjectType(ModuleEntry::class.java)
    }
}
