package com.xzakota.android

import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import java.io.File

@Suppress("unused", "MemberVisibilityCanBePrivate")
internal object AndroidResource {
    private const val TYPE_RES_STRING = "string"
    private const val TYPE_RES_STRING_ARRAY = "string-array"

    fun generateStrings(
        baseFile : File,
        name : String,
        vararg initValue : String
    ) = generate(baseFile, TYPE_RES_STRING, name, *initValue)

    fun generateArray(
        baseFile : File,
        name : String,
        vararg initValue : String
    ) = generate(baseFile, TYPE_RES_STRING_ARRAY, name, *initValue)

    fun generateArray(
        baseFile : File,
        name : String,
        initValue : List<String>
    ) = generate(baseFile, TYPE_RES_STRING_ARRAY, name, *initValue.toTypedArray())

    fun generate(baseFile : File, initTag : String, name : String, vararg initValue : String) = generate(baseFile) {
        if (initValue.isNotEmpty()) {
            if (initTag == TYPE_RES_STRING_ARRAY) {
                addElement(initTag).apply {
                    addAttribute("name", name)

                    initValue.forEach {
                        addElement("item").apply {
                            text = it
                        }
                    }
                }
            } else {
                initValue.forEach {
                    addElement(initTag).apply {
                        addAttribute("name", name)
                        text = it
                    }
                }
            }
        }
    }

    private fun generate(baseFile : File, block : Element.() -> Unit) {
        val document = DocumentHelper.createDocument()
        document.addElement("resources").run(block)

        XMLWriter(
            baseFile.writer(),
            OutputFormat.createPrettyPrint().apply {
                encoding = Charsets.UTF_8.name()
            }
        ).apply {
            write(document)
            close()
        }
    }
}
