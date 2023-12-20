package com.github.unldenis.tomljvm

fun main() {

    read("""
        # I am a comment. Hear me roar. Roar.
        key = "value" # Yeah, you can do this.
        age = -20
    """.trimIndent())
}


fun read(source: String) {

    val scanner = Scanner(source)
    println(scanner.scan())
}