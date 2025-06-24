package com.xzakota.gradle.plugin.xposed

import com.xzakota.android.xposed.XposedFramework

@Suppress("unused")
internal object DataProvider {
    lateinit var moduleConfig : XposedModuleExtension
    lateinit var xposedEntryClassName : String
    lateinit var lsposedEntryClassName : String

    fun isSupportXposed(config : XposedModuleExtension = moduleConfig) : Boolean = config.framework has XposedFramework.XPOSED

    fun isSupportLSPosed(config : XposedModuleExtension = moduleConfig) : Boolean = config.framework has XposedFramework.LSPOSED
}
