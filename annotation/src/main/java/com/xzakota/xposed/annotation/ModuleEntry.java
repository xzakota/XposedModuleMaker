package com.xzakota.xposed.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ModuleEntry {}
