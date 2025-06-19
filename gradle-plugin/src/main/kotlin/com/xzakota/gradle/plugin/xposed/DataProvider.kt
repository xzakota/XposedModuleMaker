package com.xzakota.gradle.plugin.xposed

import com.xzakota.android.xposed.XposedFramework

@Suppress("unused")
internal object DataProvider {
    lateinit var moduleConfig : XposedModuleExtension
        private set

    val isSupportXposed get() = isSupportXposed(moduleConfig)

    val isSupportLSPosed get() = isSupportLSPosed(moduleConfig)

    lateinit var xposedEntryClassName : String
    lateinit var lsposedEntryClassName : String

    fun init(config : XposedModuleExtension) {
        moduleConfig = config
    }

    fun isSupportXposed(config : XposedModuleExtension) : Boolean = config.framework has XposedFramework.XPOSED

    fun isSupportLSPosed(config : XposedModuleExtension) : Boolean = config.framework has XposedFramework.LSPOSED
}
