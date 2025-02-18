package com.xzakota.xposed.processor.writer

import kotlin.reflect.KClass

@Suppress("unused")
object LSPosedDataWriter : HookDataWriter() {
    override fun getAnnotation() : KClass<*> = com.xzakota.xposed.annotation.LSPosedModule::class

    override fun getEntryFile() : String = "META-INF/xposed/java_init.list"
}
