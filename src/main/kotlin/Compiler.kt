package com.github.unldenis.tomljvm

class Compiler<T>(clazz: Class<T>) : ASTVisitor<Unit> {

    val instance : T = clazz.newInstance()
    private val fields = clazz.declaredFields.associateBy({it.name}, {it})

    override fun visit(node: ASTNode): Unit {
        return when(node.nodeType) {
            NodeType.ASSIGNMENT -> {
                val nameField = node.tokens.first().lexeme

                val astValue = node.children.first()
                when(astValue.nodeType) {
                    NodeType.PROGRAM -> {}
                    NodeType.ASSIGNMENT -> {}
                    NodeType.ARRAY -> {
                        val arrValues = astValue.children.map { it.tokens.first().literal }
                        fields[nameField]?.also {
                            it.isAccessible = true
                            it.set(instance, arrValues)
                        }
                        Unit
                    }
                    NodeType.LITERAL -> {
                        val value = astValue.tokens.first()

                        val field = fields[nameField]
                        field?.also {
                            it.isAccessible = true
                            it.set(instance,

                                when(value.tokenType) {
                                    TokenType.FLOAT -> {
                                        val double =  value.literal as Double
                                        if(field.type.kotlin.javaPrimitiveType == Float::class.java) {
                                            double.toFloat()
                                        } else {
                                            double
                                        }
                                    }
                                    else -> { value.literal }
                                }
                            )
                        }
                        Unit
                    }
                    NodeType.COMMENT -> {}

                }
            }
            NodeType.ARRAY -> {

            }
            NodeType.LITERAL -> {

            }
            NodeType.COMMENT -> {

            }

            NodeType.PROGRAM -> {
                node.children.forEach { visit(it) }
            }
        }
    }






}