@file:Suppress("unused")

package com.xzakota.extension

import org.dom4j.Element
import org.dom4j.Namespace

internal fun Element.addNamespace(namespace : Namespace) = addNamespace(namespace.prefix, namespace.uri)
