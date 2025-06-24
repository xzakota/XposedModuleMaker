@file:Suppress("unused", "DEPRECATION")

package com.xzakota.gradle.plugin.xposed

import com.android.build.gradle.api.AndroidSourceSet
import com.xzakota.extension.safeMkdirs
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import java.io.File

abstract class GenerateModulePropertiesTask : BaseModuleGenerateTask() {
    @get:OutputDirectory
    abstract val outputDir : DirectoryProperty

    @Internal
    lateinit var sourceSets : NamedDomainObjectContainer<AndroidSourceSet>

    @Internal
    protected val lsposedAPIConfig = xposedModuleConfig.lsposed

    @Input
    protected val moduleTargetAPIVersion = lsposedAPIConfig.targetAPIVersion

    @get:Input
    protected val isModuleStaticScope = lsposedAPIConfig.isStaticScope

    init {
        description = "Generate lsposed properties"
    }

    override fun onAction() {
        val modulePropertiesDir = File(outputDir.get().asFile, "META-INF/xposed")
        modulePropertiesDir.safeMkdirs()

        File(modulePropertiesDir, "module.prop").writeText(
            """
            minApiVersion=$moduleMinAPIVersion
            targetApiVersion=$moduleTargetAPIVersion
            staticScope=$isModuleStaticScope
            """.trimIndent()
        )

        File(modulePropertiesDir, "scope.list").writeText(moduleScope.joinToString("\n"))
    }
}
