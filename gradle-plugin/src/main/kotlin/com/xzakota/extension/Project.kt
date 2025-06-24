package com.xzakota.extension

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

internal fun Project.addDependencies(module: String, version: String) {
    dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, "$module:$version")
}
