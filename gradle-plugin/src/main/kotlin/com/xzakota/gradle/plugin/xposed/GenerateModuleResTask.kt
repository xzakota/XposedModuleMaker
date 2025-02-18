package com.xzakota.gradle.plugin.xposed

import com.xzakota.LangCode
import com.xzakota.android.AndroidResource
import com.xzakota.extension.safeCreateNewFile
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import java.io.File

@Suppress("unused")
abstract class GenerateModuleResTask : BaseModuleGenerateTask() {
    @get:OutputDirectory
    abstract val outputDir : DirectoryProperty

    init {
        description = "Generate module res values"
    }

    override fun onAction() {
        val resDir = outputDir.get().asFile

        val generateStringAction : (String, String) -> Unit = action@{ langCode, value ->
            if (langCode.isEmpty()) {
                return@action
            }

            val stringRes = "values" + if (langCode == LangCode.LANG_CODE_DEFAULT) {
                ""
            } else {
                "-${langCode}"
            } + "/strings.xml"

            File(resDir, stringRes).apply {
                safeCreateNewFile()
                AndroidResource.generateStrings(this, moduleResGenerator.resID.descriptionResID, value)
            }
        }

        if (moduleDescriptionRes.isNotEmpty()) {
            resDir.listFiles()?.forEach {
                if (it.isDirectory) {
                    it.deleteRecursively()
                }
            }
            moduleDescriptionRes.forEach(generateStringAction)
        } else {
            generateStringAction(LangCode.LANG_CODE_DEFAULT, moduleDescription)
            resDir.listFiles()?.forEach {
                if (it.isDirectory && it.name != "values") {
                    it.deleteRecursively()
                }
            }
        }

        File(resDir, "values/array.xml").apply {
            safeCreateNewFile()
            AndroidResource.generateArray(this, moduleResGenerator.resID.scopeResID, moduleScope)
        }
    }
}
