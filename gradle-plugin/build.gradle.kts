plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.vanniktech.maven.publish)
}

gradlePlugin {
    plugins {
        create(rootProject.name) {
            id = "com.xzakota.xposed"
            implementationClass = "com.xzakota.gradle.plugin.xposed.GradlePluginForXposed"
        }
    }
}

dependencies {
    compileOnly(libs.android.tools)
    implementation(libs.dom4j)
}
