package com.xzakota.gradle.plugin.xposed

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Suppress("unused")
abstract class BaseModuleGenerateTask : DefaultTask() {
    init {
        group = "xposed"
    }

    @Internal
    protected val xposedModuleConfig = DataProvider.moduleConfig

    @get:Input
    protected val moduleMinAPIVersion = xposedModuleConfig.minAPIVersion

    @get:Input
    protected val moduleDescription = xposedModuleConfig.description

    @get:Input
    protected val moduleDescriptionRes = xposedModuleConfig.descriptionRes

    @get:Input
    protected val moduleScope = xposedModuleConfig.scope

    @get:Input
    protected val moduleFrameworkSupportList = xposedModuleConfig.framework.supportList

    @Internal
    protected val moduleResGenerator = xposedModuleConfig.resGenerator

    @get:Input
    protected val isIncludeDependencies = xposedModuleConfig.isIncludeDependencies

    @TaskAction
    fun run() {
        onAction()
    }

    protected abstract fun onAction()
}
