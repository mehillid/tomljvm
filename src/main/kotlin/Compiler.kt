package com.github.unldenis.tomljvm

class Compiler<T>(parentClass: Class<T>) : ASTVisitor<Unit> {

    private var clazz: Class<*> = parentClass
    val parentInstance = parentClass.newInstance()
    private val parentFields = loadFields(clazz)

    private var instance : Any = parentInstance as Any
    private var fields = parentFields

    private fun loadFields(cl: Class<*>) = cl.declaredFields.associateBy({it.name.replace("_", "-")}, {it})

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
                    NodeType.TABLE -> throw TomlJvmException("Entry value cannot be a table at $astValue")
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

            NodeType.TABLE -> {
                val nameTable = node.tokens.first().lexeme
                val field = parentFields[nameTable] ?: throw TomlJvmException("Table of name $nameTable not found")
                val typeField = field.type

                clazz = typeField
                instance = clazz.newInstance()
                fields = loadFields(clazz)

                field.isAccessible = true
                field.set(parentInstance, instance)
            }
        }
    }






}