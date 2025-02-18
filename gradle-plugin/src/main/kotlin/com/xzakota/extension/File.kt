@file:Suppress("unused")

package com.xzakota.extension

import java.io.File

internal fun File.safeCreateNewFile() {
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }
}
