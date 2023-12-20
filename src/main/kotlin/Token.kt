package com.github.unldenis.tomljvm

data class Token(val tokenType: TokenType, val lexeme : String = "", val literal: Any? = null, val line: Int, val column: Int)
enum class TokenType {
    EQUAL,
    DOT, COMMA,

    IDENTIFIER, COMMENT,

    L_SQUARE_BRACKET, R_SQUARE_BRACKET,

    STRING, INTEGER, FLOAT, BOOLEAN, DATETIME,



    EOF
}

