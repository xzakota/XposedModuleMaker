package com.xzakota.hook.example.startup

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.xzakota.hook.example.BuildConfig
import com.xzakota.xposed.annotation.ModuleEntry
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import kotlin.random.Random

private lateinit var module : XposedModule

@ModuleEntry
class LSPInitEntry(
    base : XposedInterface,
    param : XposedModuleInterface.ModuleLoadedParam
) : XposedModule(base, param) {
    init {
        module = this
        log("OnCreate LSPModuleMainEntry")
    }

    override fun onPackageLoaded(param : XposedModuleInterface.PackageLoadedParam) {
        log("OnPackageLoaded: " + param.packageName)
        log("PackageClassLoader: " + param.classLoader)
        log("HostPath: " + applicationInfo.sourceDir)
        log("----------")

        if (!param.isFirstPackage) {
            return
        }

        if (param.packageName == BuildConfig.APPLICATION_ID) {
            return
        }

        @SuppressLint("DiscouragedPrivateApi")
        val applicationAttachMethod = Application::class.java.getDeclaredMethod("attach", Context::class.java)
        hook(applicationAttachMethod, MyHooker::class.java)
    }

    @XposedHooker
    class MyHooker(private val magic : Int) : XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeInvocation(callback : XposedInterface.BeforeHookCallback) : MyHooker {
                val key = Random.nextInt()
                val appContext = callback.args[0]
                module.log("beforeInvocation: key = $key")
                module.log("app context: $appContext")
                return MyHooker(key)
            }

            @JvmStatic
            @AfterInvocation
            fun afterInvocation(callback : XposedInterface.AfterHookCallback, context : MyHooker) {
                module.log("afterInvocation: key = ${context.magic}")
            }
        }
    }
}
