package com.xzakota.gradle.plugin.xposed

import com.xzakota.android.xposed.XposedFramework
import com.xzakota.extension.safeCreateNewFile
import org.gradle.api.tasks.Internal
import java.io.File

@Suppress("unused")
abstract class GenerateModuleConfigClassTask : BaseModuleGenerateTask() {
    @Internal
    lateinit var packageName : String

    init {
        description = "Generate module config class"
    }

    override fun onAction() {
        File(outputs.files.firstOrNull() ?: return, "${packageName.replace(".", "/")}/ModuleConfig.kt").apply {
            safeCreateNewFile()
            writeText(
                """
                package $packageName
                
                import ${XposedFramework::class.java.name}
                
                object ModuleConfig {
                    @JvmField
                    val supportFramework = arrayOf(${moduleFrameworkSupportList.joinToString(", ") { "XposedFramework.$it" }})
                    
                    @JvmField
                    val scope = arrayOf(${moduleScope.joinToString(", ") { "\"$it\"" }})
                }
                """.trimIndent()
            )
        }
    }
}
