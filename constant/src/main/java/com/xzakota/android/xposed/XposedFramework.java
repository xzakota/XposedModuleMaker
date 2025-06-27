package com.xzakota.android.xposed;

@SuppressWarnings("unused")
public enum XposedFramework {
    XPOSED("Xposed"), LSPOSED("LSPosed");

    private final String name;

    XposedFramework(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
