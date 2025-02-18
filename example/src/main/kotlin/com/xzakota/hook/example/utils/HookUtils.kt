package com.xzakota.hook.example.utils

import androidx.annotation.Keep

object HookUtils {
    @Keep
    var isSelfModuleActivated = false

    @Keep
    var xposedAPIVersion = -1
}