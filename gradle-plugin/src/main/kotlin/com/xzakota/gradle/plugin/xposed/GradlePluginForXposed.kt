@file:Suppress("unused", "DEPRECATION")

package com.xzakota.gradle.plugin.xposed

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts.Scope
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.xzakota.android.xposed.XposedAPIVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.Locale

@Suppress("unused")
class GradlePluginForXposed : Plugin<Project> {
    override fun apply(project : Project) {
        val isAndroidAppProject = project.plugins.hasPlugin(AppPlugin::class.java)
        if (!isAndroidAppProject) {
            return
        }

        project.extensions.create("xposedModule", XposedModuleExtension::class.java)
        registerTask(project)
    }

    private fun registerTask(project : Project) {
        val moduleConfig = project.extensions.getByName("xposedModule") as? XposedModuleExtension
        if (moduleConfig == null || !moduleConfig.isXposedModule) {
            return
        }

        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
        androidComponents?.onVariants { variant : Variant ->
            val tasks = project.tasks
            val variantTitleName = variant.name.capitalize(Locale.ROOT)

            var taskName = "generate${variantTitleName}XposedModuleManifest"
            if (tasks.findByPath(taskName) == null) {
                val task = tasks.register(taskName, GenerateModuleManifestTask::class.java)
                variant.sources.manifests.addGeneratedManifestFile(
                    task, GenerateModuleManifestTask::outputFile
                )
            }

            if (DataProvider.isSupportLSPosed(moduleConfig)) {
                taskName = "generate${variantTitleName}XposedModuleProperties"
                if (tasks.findByPath(taskName) == null) {
                    val task = tasks.register(taskName, GenerateModulePropertiesTask::class.java)
                    variant.sources.resources?.addGeneratedSourceDirectory(
                        task, GenerateModulePropertiesTask::outputDir
                    )
                }

                if (moduleConfig.lsposed.targetAPIVersion >= XposedAPIVersion.XP_API_100) {
                    variant.packaging.resources.merges.add("META-INF/xposed/*")
                }
            }

            taskName = "generate${variantTitleName}XposedEntryClass"
            if (tasks.findByPath(taskName) == null) {
                val task = tasks.register(taskName, GenerateModuleEntryTask::class.java)
                variant.artifacts.forScope(
                    if (moduleConfig.isIncludeDependencies) {
                        Scope.ALL
                    } else {
                        Scope.PROJECT
                    }
                ).use(task).toTransform(
                    ScopedArtifact.CLASSES,
                    GenerateModuleEntryTask::allJars,
                    GenerateModuleEntryTask::allDirectories,
                    GenerateModuleEntryTask::classOutput
                )

                variant.sources.resources?.addGeneratedSourceDirectory(
                    task, GenerateModuleEntryTask::entryOutput
                )
            }
        }

        val androidExtensions = project.extensions.getByName("android") as BaseAppModuleExtension
        project.afterEvaluate {
            DataProvider.init(moduleConfig)

            val projectGeneratedDir = project.layout.buildDirectory.dir("generated")
            val generatedBaseDir = projectGeneratedDir.get().dir("xposed")

            androidExtensions.applicationVariants.forEach { variant ->
                val variantDirName = variant.dirName
                val variantTitleName = variant.name.capitalize(Locale.ROOT)

                val generatedResDir = generatedBaseDir.dir("resValues/$variantDirName")

                val taskName = "generate${variantTitleName}XposedModuleResource"
                if (tasks.findByPath(taskName) == null) {
                    val task = tasks.register(taskName, GenerateModuleResTask::class.java) {
                        outputDir.set(generatedResDir)
                    }
                    variant.registerGeneratedResFolders(project.files(generatedResDir).builtBy(task))
                    variant.mergeResourcesProvider.dependsOn(task)
                }
            }
        }
    }
}
