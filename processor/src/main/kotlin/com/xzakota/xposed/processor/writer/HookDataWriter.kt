package com.xzakota.xposed.processor.writer

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlin.reflect.KClass

@Suppress("unused")
abstract class HookDataWriter : BaseWriter() {
    override fun generateAndWrite(environment : SymbolProcessorEnvironment, resolver : Resolver) {
        var isGenerateOnce = true
        val annotationName = getAnnotation().qualifiedName.toString()
        resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { type ->
                if (!isGenerateOnce) {
                    throw Exception("Multiple class annotated with $annotationName")
                }

                environment.codeGenerator.createNewFileByPath(
                    Dependencies(false), getEntryFile(), ""
                ).bufferedWriter().use { writer ->
                    writer.write(requireNotNull(type.qualifiedName?.asString()))
                }

                isGenerateOnce = false
            }
    }

    abstract fun getAnnotation() : KClass<*>

    abstract fun getEntryFile() : String

    // TODO
    fun isAllowListEntry() : Boolean = false
}
