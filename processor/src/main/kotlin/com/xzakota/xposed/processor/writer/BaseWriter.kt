package com.xzakota.xposed.processor.writer

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

@Suppress("unused")
abstract class BaseWriter {
    abstract fun generateAndWrite(environment : SymbolProcessorEnvironment, resolver : Resolver)
}
