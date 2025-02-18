@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

subprojects {
    if (name == "example") {
        return@subprojects
    }

    group = "com.xzakota.xposed"
    version = "0.1.0"

    plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    plugins.withType(KotlinBasePlugin::class.java) {
        extensions.configure(KotlinJvmProjectExtension::class.java) {
            jvmToolchain(21)
        }
    }

    plugins.withType(MavenPublishPlugin::class.java) {
        extensions.configure(MavenPublishBaseExtension::class.java) {
            configureBasedOnAppliedPlugins(javadocJar = true)

            coordinates(group.toString(), name, version.toString())

            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
            signAllPublications()
        }
    }
}
