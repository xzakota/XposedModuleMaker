package com.xzakota.gradle.plugin.xposed

import com.xzakota.android.xposed.XposedFramework

internal object DataProvider {
    val moduleConfig = hashMapOf<String, XposedModuleExtension>()

    lateinit var xposedEntryClassName : String
    lateinit var lsposedEntryClassName : String

    fun isSupportXposed(config : XposedModuleExtension) : Boolean = config.framework has XposedFramework.XPOSED
    fun isSupportLSPosed(config : XposedModuleExtension) : Boolean = config.framework has XposedFramework.LSPOSED
}
