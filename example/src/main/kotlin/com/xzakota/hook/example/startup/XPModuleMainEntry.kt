package com.xzakota.hook.example.startup

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.xzakota.hook.example.BuildConfig
import com.xzakota.hook.example.utils.HookUtils
import com.xzakota.xposed.annotation.XposedModule
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

@XposedModule
class XPModuleMainEntry : IXposedHookZygoteInit, IXposedHookLoadPackage {
    override fun initZygote(startupParam : IXposedHookZygoteInit.StartupParam) {
        log("OnCreate XPModuleMainEntry")
    }

    override fun handleLoadPackage(lpparam : XC_LoadPackage.LoadPackageParam) {
        log("OnPackageLoaded: " + lpparam.packageName)
        log("PackageClassLoader: " + lpparam.classLoader)
        log("HostPath: " + lpparam.appInfo.sourceDir)
        log("----------")

        if (!lpparam.isFirstApplication) {
            return
        }

        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            val utils = XposedHelpers.findClassIfExists(HookUtils::class.java.name, lpparam.classLoader)
            XposedHelpers.setStaticBooleanField(utils, "isSelfModuleActivated", true)
            XposedHelpers.setStaticIntField(utils, "xposedAPIVersion", XposedBridge.getXposedVersion())

            return
        }

        @SuppressLint("DiscouragedPrivateApi")
        val applicationAttachMethod = Application::class.java.getDeclaredMethod("attach", Context::class.java)
        XposedBridge.hookMethod(applicationAttachMethod, object : XC_MethodHook() {
            override fun beforeHookedMethod(param : MethodHookParam) {
                val appContext = param.args[0]

                log("beforeHookedMethod")
                log("app context: $appContext")
            }

            override fun afterHookedMethod(param : MethodHookParam) {
                log("afterHookedMethod")
            }
        })
    }

    private fun log(text : String) = XposedBridge.log(text)
}
