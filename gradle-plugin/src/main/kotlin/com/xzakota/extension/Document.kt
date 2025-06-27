package com.xzakota.extension

import org.dom4j.Document
import org.dom4j.Element

internal fun Document.addElement(name : String, block : Element.() -> Unit) = addElement(name).apply(block)
