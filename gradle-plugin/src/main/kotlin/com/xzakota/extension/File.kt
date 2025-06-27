package com.xzakota.extension

import java.io.File

internal fun File.safeMkdirs() {
    if (!exists()) {
        mkdirs()
    }
}

internal fun File.safeCreateNewFile() {
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }
}
