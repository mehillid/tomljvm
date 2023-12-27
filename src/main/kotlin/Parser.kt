package com.github.unldenis.tomljvm


class Parser(private val tokens : List<Token>) {

    private var current = 0


    fun error(message: String) : TomlJvmException {
        return TomlJvmException("$message, at line ${peek().line}, column ${peek().column}")
    }

    fun parse(): ASTNode {
        val statements: MutableList<ASTNode> = mutableListOf()
        while (!isAtEnd()) {
            if(match(TokenType.COMMENT)) {
                statements.add(ASTNode(NodeType.COMMENT, previous()))
            }
            statements.add(assignment())
        }

        return ASTNode(NodeType.PROGRAM, children = statements)
    }

    fun assignment() : ASTNode {
        val identifier = consume(TokenType.IDENTIFIER, "Expect variable name")
        consume(TokenType.EQUAL, "Expect equal")

        val value = array()
        return ASTNode(NodeType.ASSIGNMENT, tokens = listOf(identifier), children = listOf(value))
    }

    fun array() : ASTNode {
        if(match(TokenType.L_SQUARE_BRACKET)) {
            var comma = false
            val elements = mutableListOf<ASTNode>()
            var firstType : TokenType? = null
            while (peek().tokenType != TokenType.R_SQUARE_BRACKET) {
                if(comma) {
                    if(!match(TokenType.COMMA)) {
                        throw error("Missing comma")
                    }
                    comma = false
                    continue
                }
                val literal = primary()

                if(literal.nodeType != NodeType.LITERAL) {
                    throw error("Array value must be a literal not ${literal.nodeType}")
                }
                val token = literal.tokens[0]

                if(firstType == null) {
                    firstType = token.tokenType
                } else {
                    if(firstType != token.tokenType) {
                        throw error("Element ${token.lexeme} must be ${firstType.name}")
                    }
                }
                comma = true
                elements.add(literal)

            }
            advance()

            return ASTNode(NodeType.ARRAY, children = elements)

        }
        return primary()
    }

    fun primary() : ASTNode {
        if(match(TokenType.BOOLEAN) || match(TokenType.STRING) || match(TokenType.INTEGER) || match(TokenType.FLOAT)) {
            return ASTNode(NodeType.LITERAL, previous())
        }


        throw error("Expect expression, found instead ${peek().tokenType}")
    }

    /*
    UTILS
     */

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()

        throw error(message)
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }

        return false
    }
    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().tokenType === type
    }
    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }
    private fun isAtEnd(): Boolean {
        return peek().tokenType === TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }
}