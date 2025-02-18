package com.xzakota.android

import org.dom4j.Namespace
import org.dom4j.QName

@Suppress("unused")
internal object AndroidXML {
    @JvmField
    val NAMESPACE_ANDROID = Namespace("android", "http://schemas.android.com/apk/res/android")

    @JvmField
    val QUALIFIED_NAME_NAME = QName.get("name", NAMESPACE_ANDROID)

    @JvmField
    val QUALIFIED_NAME_VALUE = QName.get("value", NAMESPACE_ANDROID)

    @JvmField
    val QUALIFIED_NAME_RESOURCE = QName.get("resource", NAMESPACE_ANDROID)

    @JvmField
    val QUALIFIED_NAME_DESCRIPTION = QName.get("description", NAMESPACE_ANDROID)
}
