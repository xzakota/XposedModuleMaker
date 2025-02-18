@file:OptIn(KspExperimental::class)

import com.google.devtools.ksp.KspExperimental

plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.vanniktech.maven.publish)
}

ksp {
    useKsp2 = true
}

dependencies {
    implementation(project(":annotation"))

    compileOnly(libs.ksp.symbol.processing.api)
    implementation(libs.auto.service)
    ksp(libs.auto.service.ksp)
}
