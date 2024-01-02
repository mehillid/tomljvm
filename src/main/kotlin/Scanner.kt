package com.github.unldenis.tomljvm

import javax.print.DocFlavor.STRING




class Scanner(val source: String) {

    val UNICODE_END : Char = '\u0000'

    private val tokens: MutableList<Token> = mutableListOf()

    private var start = 0
    private var current = 0
    private var line = 1

    private var column = 0

    fun error(message: String) : TomlJvmException = TomlJvmException("Unexpected error at line $line, column $column: $message")

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun advance(): Char {
        column++
        return source[current++]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line, column))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        column++
        current++
        return true
    }
    private fun peek(): Char {
        if (isAtEnd()) return UNICODE_END
        return source[current]
    }


    private fun prev() : Char {
        if (isAtEnd()) return UNICODE_END
        return source[current - 1]
    }

    fun scan() : List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, line = line, column = column))
        return tokens
    }

    fun scanToken() {
        when(val c = advance()) {
            '=' -> addToken(TokenType.EQUAL)
            '.' -> addToken(TokenType.DOT)
            ',' -> addToken(TokenType.COMMA)
            '[' -> addToken(TokenType.L_SQUARE_BRACKET)
            ']' -> addToken(TokenType.R_SQUARE_BRACKET)
            '{' -> addToken(TokenType.L_BRACKET)
            '}' -> addToken(TokenType.R_BRACKET)
            '#' -> {
                while (peek() != '\n' && !isAtEnd()) {
                    advance()
                }
                addToken(TokenType.COMMENT)
            }
            ' ', '\r', '\t' -> {

            }
            '\n' -> {
                line++
                column = 0
            }
            '"' -> {
                while (peek() != '"' && !isAtEnd()) {
                    if (peek() == '\n') {
                        line++
                        column = 0
                    }
                    advance()
                }

                if (isAtEnd()) {
                    throw error("Unterminated string.")
                }

                // The closing ".
                advance()

                // Trim the surrounding quotes.
                val value = source.substring(start + 1, current - 1)
                addToken(TokenType.STRING, value)
            }
            else -> {

                if (c.isJavaIdentifierStart() && !isAtEnd()) {
                    advance()

                    while ((peek().isJavaIdentifierPart() || peek() == '-') && !isAtEnd()) {
                        advance()
                    }

                    when(val value = source.substring(start, current)) {
                        "true" -> addToken(TokenType.BOOLEAN, true)
                        "false" -> addToken(TokenType.BOOLEAN, false)
                        else -> addToken(TokenType.IDENTIFIER, value)
                    }

                } else {
                    if(c == '+' || c == '-' || c.isDigit()) {

                        var isFloat = false
                        while (!isAtEnd()) {
                            if(peek().isDigit()) {
                                advance()

                            } else if(peek() == '.') {
                                isFloat = true
                                advance()

                            } else {
                                break
                            }
                        }



                        val value = source.substring(start, current)
                        if(isFloat) {
                            addToken(TokenType.FLOAT, value.toDouble())
                        } else {
                            addToken(TokenType.INTEGER, value.toInt())
                        }

                    } else {

                        throw error("Unexpected character")
                    }

                }

            }
        }
    }

}