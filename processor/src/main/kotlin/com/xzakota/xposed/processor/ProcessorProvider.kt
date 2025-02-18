package com.xzakota.xposed.processor

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.xzakota.xposed.processor.writer.LSPosedDataWriter
import com.xzakota.xposed.processor.writer.XposedDataWriter

@Suppress("unused")
@AutoService(SymbolProcessorProvider::class)
class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment : SymbolProcessorEnvironment) : SymbolProcessor = object : SymbolProcessor {
        var isProcess = false
        val enabledWriter = arrayOf(XposedDataWriter, LSPosedDataWriter)

        override fun process(resolver : Resolver) : List<KSAnnotated> {
            val result = emptyList<KSAnnotated>()

            if (isProcess) {
                return result
            }
            isProcess = true

            enabledWriter.forEach {
                it.generateAndWrite(environment, resolver)
            }

            return result
        }
    }
}
