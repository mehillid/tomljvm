package com.github.unldenis.tomljvm

fun <T : Enum<T>> getEnumFromString(clazz: Class<T>, string: String) : T {
    return java.lang.Enum.valueOf(clazz, string)
}