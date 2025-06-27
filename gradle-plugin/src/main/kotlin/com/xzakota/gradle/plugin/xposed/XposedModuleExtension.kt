package com.xzakota.gradle.plugin.xposed

import com.xzakota.LangCode
import com.xzakota.android.xposed.XposedAPIVersion
import com.xzakota.android.xposed.XposedFramework
import org.gradle.api.Action

internal typealias StringList = ArrayList<String>
internal typealias StringRes = HashMap<String, String>

@Suppress("unused")
open class XposedModuleExtension {
    // 是否为 Xposed 模块
    var isXposedModule = true

    // 最低 API
    var minAPIVersion = XposedAPIVersion.XP_API_82

    // 模块介绍
    var description = ""
    internal val descriptionRes = Description()

    // 模块作用域(非原生 Xposed 管理器支持)
    val scope = StringList()

    // 支持框架
    internal val framework = Framework()

    // LSPosed API
    internal val lsposed = LSPosedAPI()

    // R 资源生成控制
    internal val resGenerator = ResGenerator()

    // 是否检索依赖库
    var isIncludeDependencies = false

    // 是否检索依赖库
    var isGenerateConfigClass = false

    fun description(action : Action<Description>) {
        action.execute(descriptionRes)
    }

    fun framework(action : Action<Framework>) {
        action.execute(framework)
    }

    fun scope(action : Action<StringList>) {
        action.execute(scope)
    }

    fun lsposed(action : Action<LSPosedAPI>) {
        action.execute(lsposed)
    }

    fun resGenerator(action : Action<ResGenerator>) {
        action.execute(resGenerator)
    }

    override fun toString() : String = """
        XposedModuleExtension {
            isXposedModule => $isXposedModule
            minAPIVersion => $minAPIVersion
            framework => $framework
        }
    """.trimIndent()

    open class Description internal constructor() : StringRes() {
        @JvmOverloads
        fun resString(value : String, langCode : String = LangCode.LANG_CODE_DEFAULT) {
            put(langCode, value)
        }
    }

    open class Framework internal constructor() {
        internal val supportList = mutableListOf(XposedFramework.XPOSED)

        fun add(element : XposedFramework) {
            supportList.add(element)
        }

        fun remove(element : XposedFramework) {
            supportList.remove(element)
        }

        fun only(element : XposedFramework) {
            supportList.clear()
            add(element)
        }

        fun isEmpty() : Boolean = supportList.isEmpty()

        internal infix fun has(element : XposedFramework) : Boolean = supportList.contains(element)

        override fun toString() : String = supportList.toString()
    }

    open class LSPosedAPI internal constructor() {
        /**
         * 新 XSharedPreferences
         *
         * 将在 LSPosed-2.1.0 停止支持
         */
        var isNewXSharedPreferences = false

        // 目标 API
        var targetAPIVersion = XposedAPIVersion.XP_API_82

        // 静态作用域
        var isStaticScope = false
    }

    open class ResGenerator internal constructor() {
        // 资源 ID
        internal val resID = ResID(ResID.ID_DESCRIPTION, ResID.ID_SCOPE)

        fun resID(action : Action<ResID>) {
            action.execute(resID)
        }

        open class ResID internal constructor(var descriptionResID : String, var scopeResID : String) {
            companion object {
                const val ID_DESCRIPTION = "__xposed_module_description"
                const val ID_SCOPE = "__xposed_module_scope"
            }
        }
    }
}
