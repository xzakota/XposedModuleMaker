package com.xzakota.xposed.processor.writer

import kotlin.reflect.KClass

@Suppress("unused")
object XposedDataWriter : HookDataWriter() {
    override fun getAnnotation() : KClass<*> = com.xzakota.xposed.annotation.XposedModule::class

    override fun getEntryFile() : String = "assets/xposed_init"
}
