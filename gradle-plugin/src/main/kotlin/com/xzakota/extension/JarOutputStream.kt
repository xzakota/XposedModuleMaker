@file:Suppress("unused")

package com.xzakota.extension

import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

internal fun JarOutputStream.createFile(name: String, data: ByteArray) = runCatching {
    putNextEntry(JarEntry(name.replace(File.separatorChar, '/')))
    write(data)
    closeEntry()
}

internal fun JarOutputStream.createDirectory(name: String) = runCatching {
    putNextEntry(JarEntry(name.replace(File.separatorChar, '/')))
    closeEntry()
}
