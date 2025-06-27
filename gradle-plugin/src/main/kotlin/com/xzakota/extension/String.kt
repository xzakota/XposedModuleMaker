package com.xzakota.extension

import java.util.Locale

internal fun String.capitalizeFirstChar() = replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(Locale.ROOT)
    } else {
        it.toString()
    }
}
