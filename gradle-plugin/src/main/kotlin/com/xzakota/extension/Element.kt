package com.xzakota.extension

import org.dom4j.Element
import org.dom4j.Namespace

internal fun Element.addNamespace(namespace : Namespace) = addNamespace(namespace.prefix, namespace.uri)

internal fun Element.addElement(name : String, block : Element.() -> Unit) = addElement(name).apply(block)
