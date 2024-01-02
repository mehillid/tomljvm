package com.github.unldenis.tomljvm

import kotlin.jvm.Throws


class TomlJvm  {
    companion object {

        @JvmStatic
        @Throws(TomlJvmException::class)
        fun <T> read(source: String, clazz: Class<T>) : T {

            val scanner = Scanner(source)
            val tokens = scanner.scan()

            val parser = Parser(tokens)

            val visitor = Compiler(clazz)
            visitor.visit(parser.parse())

            return visitor.parentInstance
        }
    }
}