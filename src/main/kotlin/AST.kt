package com.github.unldenis.tomljvm

enum class NodeType {

    PROGRAM,

    TABLE,

    ASSIGNMENT,

    ARRAY,
    LITERAL,

    COMMENT
}

enum class Property {

}

interface ASTVisitor<E> {
    fun visit(node: ASTNode): E
}


data class ASTNode(val nodeType: NodeType, val tokens : List<Token> = emptyList(), val children : List<ASTNode> = emptyList(),
    val properties: MutableMap<Property, Any> = mutableMapOf()
) {

    constructor(nodeType: NodeType, token: Token) : this(nodeType, tokens = listOf(token))


    fun hasProperty(property: Property) : Boolean {
        return properties.contains(property)
    }



}