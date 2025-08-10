package com.xzakota.gradle.plugin.xposed

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts.Scope
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.xzakota.android.xposed.XposedAPIVersion
import com.xzakota.extension.addDependencies
import com.xzakota.extension.capitalizeFirstChar
import com.xzakota.xposed.BuildConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

@Suppress("unused")
class GradlePluginForXposed : Plugin<Project> {
    override fun apply(project : Project) {
        val isAndroidAppProject = project.plugins.hasPlugin(AppPlugin::class.java)
        if (!isAndroidAppProject) {
            return
        }

        registerTask(project, project.extensions.create("xposedModule", XposedModuleExtension::class.java))
    }

    private fun registerTask(project : Project, moduleConfig : XposedModuleExtension) {
        project.extensions.getByType(AndroidComponentsExtension::class.java).onVariants { variant : Variant ->
            if (!moduleConfig.isXposedModule) {
                return@onVariants
            }

            val tasks = project.tasks
            val variantTitleName = variant.name.capitalizeFirstChar()

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

        val androidExtensions = project.extensions.getByType(AppExtension::class.java)
        project.afterEvaluate {
            if (!moduleConfig.isXposedModule) {
                return@afterEvaluate
            }

            DataProvider.moduleConfig[project.name] = moduleConfig

            val projectGeneratedDir = project.layout.buildDirectory.dir("generated")
            val generatedBaseDir = projectGeneratedDir.get().dir("xposed")

            androidExtensions.applicationVariants.forEach { variant ->
                val variantDirName = variant.dirName
                val variantTitleName = variant.name.capitalizeFirstChar()

                var taskName = "generate${variantTitleName}XposedModuleResource"
                if (tasks.findByPath(taskName) == null) {
                    val generatedResDir = generatedBaseDir.dir("resValues/$variantDirName")
                    val task = tasks.register(taskName, GenerateModuleResTask::class.java) {
                        outputDir.set(generatedResDir)
                    }
                    variant.registerGeneratedResFolders(project.files(generatedResDir).builtBy(task))
                }

                if (moduleConfig.isGenerateConfigClass) {
                    taskName = "generate${variantTitleName}XposedModuleConfigClass"
                    if (tasks.findByPath(taskName) == null) {
                        val generatedJavaDir = generatedBaseDir.dir("source/$variantDirName").asFile
                        val task = tasks.register(taskName, GenerateModuleConfigClassTask::class.java) {
                            packageName = variant.applicationId
                            outputs.dir(generatedJavaDir)
                        }

                        val kspTask = tasks.findByPath("ksp${variantTitleName}Kotlin")
                        if (kspTask != null) {
                            task.get().mustRunAfter(kspTask)
                        }

                        val compileKotlinTask = tasks.findByPath("compile${variantTitleName}Kotlin")
                        if (compileKotlinTask != null) {
                            compileKotlinTask.dependsOn(task)
                        } else {
                            tasks.getByPath("compile${variantTitleName}JavaWithJavac").dependsOn(task)
                        }

                        variant.registerJavaGeneratingTask(task, generatedJavaDir)
                        androidExtensions.sourceSets[variant.name].apply {
                            java.setSrcDirs(java.srcDirs + generatedJavaDir)
                        }
                    }

                    project.addDependencies("com.xzakota.xposed:constant", BuildConfig.VERSION)
                }
            }
        }
    }
}
