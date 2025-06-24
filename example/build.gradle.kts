import com.xzakota.android.xposed.XposedAPIVersion
import com.xzakota.android.xposed.XposedFramework

plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.xposed.module.maker)
}

android {
    namespace = "com.xzakota.hook.example"
    compileSdk = 35

    defaultConfig {
        applicationId = namespace
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

xposedModule {
    minAPIVersion = XposedAPIVersion.XP_API_100
    description = "Xposed Example"

    framework {
        add(XposedFramework.LSPOSED)
    }

    lsposed {
        targetAPIVersion = minAPIVersion
        isStaticScope = false
    }

    scope += listOf(
        "com.android.settings"
    )

    isGenerateConfigClass = true
}

dependencies {
    // https://api.xposed.info
    compileOnly(libs.xposed.api)

    // https://github.com/libxposed
    compileOnly(files("libs/libxposed-api.aar"))
    implementation(files("libs/libxposed-service.aar"))

    // https://github.com/xzakota/XposedModuleMaker
    compileOnly(libs.xposed.module.maker.annotation)
}
