package com.xzakota.gradle.plugin.xposed

import com.xzakota.android.AndroidXML.NAMESPACE_ANDROID
import com.xzakota.android.AndroidXML.QUALIFIED_NAME_DESCRIPTION
import com.xzakota.android.AndroidXML.QUALIFIED_NAME_NAME
import com.xzakota.android.AndroidXML.QUALIFIED_NAME_RESOURCE
import com.xzakota.android.AndroidXML.QUALIFIED_NAME_VALUE
import com.xzakota.extension.addElement
import com.xzakota.extension.addNamespace
import com.xzakota.gradle.plugin.xposed.DataProvider.isSupportLSPosed
import com.xzakota.gradle.plugin.xposed.DataProvider.isSupportXposed
import com.xzakota.gradle.plugin.xposed.DataProvider.moduleConfig
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile

abstract class GenerateModuleManifestTask : BaseModuleGenerateTask() {
    @get:OutputFile
    abstract val outputFile : RegularFileProperty

    init {
        description = "Generate module manifest"
    }

    override fun onAction() {
        val resID = moduleConfig.resGenerator.resID
        val document = DocumentHelper.createDocument()

        document.addElement("manifest") {
            addNamespace(NAMESPACE_ANDROID)

            addElement("application") {
                val isSupportXposed = isSupportXposed()
                val isSupportLSPosed = isSupportLSPosed()

                if (isSupportLSPosed) {
                    addAttribute(QUALIFIED_NAME_DESCRIPTION, "@string/${resID.descriptionResID}")
                }

                addMetaDataElementWithValue("xposedmodule", "true", isSupportXposed)

                addMetaDataElement("xposeddescription", isSupportXposed) {
                    if (moduleConfig.descriptionRes.isNotEmpty()) {
                        it.addAttribute(QUALIFIED_NAME_RESOURCE, "@string/${resID.descriptionResID}")
                    } else {
                        it.addAttribute(QUALIFIED_NAME_VALUE, moduleConfig.description)
                    }
                }

                addMetaDataElementWithValue("xposedminversion", moduleConfig.minAPIVersion, isSupportXposed)

                addMetaDataElementWithResource(
                    "xposedscope",
                    "@array/${resID.scopeResID}",
                    isSupportXposed && moduleConfig.scope.isNotEmpty()
                )

                addMetaDataElementWithResource(
                    "xposedsharedprefs",
                    "true",
                    isSupportXposed && isSupportLSPosed && moduleConfig.lsposed.isNewXSharedPreferences
                )
            }
        }

        XMLWriter(
            outputFile.get().asFile.writer(),
            OutputFormat.createPrettyPrint().apply {
                encoding = Charsets.UTF_8.name()
            }
        ).apply {
            write(document)
            close()
        }
    }

    private fun Element.addMetaDataElementWithValue(
        name : String,
        value : Any,
        isInvokeAction : Boolean = true
    ) = addMetaDataElement(name, isInvokeAction) {
        it.addAttribute(QUALIFIED_NAME_VALUE, value.toString())
    }

    private fun Element.addMetaDataElementWithResource(
        name : String,
        resource : String,
        isInvokeAction : Boolean = true
    ) = addMetaDataElement(name, isInvokeAction) {
        it.addAttribute(QUALIFIED_NAME_RESOURCE, resource)
    }

    private fun Element.addMetaDataElement(
        name : String,
        isInvokeAction : Boolean = true,
        action : (Element) -> Unit
    ) {
        if (!isInvokeAction) {
            return
        }

        val element = addElement("meta-data") {
            addAttribute(QUALIFIED_NAME_NAME, name)
        }

        action(element)
    }
}
