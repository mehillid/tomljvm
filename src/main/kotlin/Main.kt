package com.github.unldenis.tomljvm

class Test() {

    lateinit var name : String
    private var age : Int? = null
    private var height : Float? = null
    private var numbers : List<Int>? = null
    private var cellnumbers : List<String>? = null
    override fun toString(): String {
        return "Test(name='$name', age=$age, height=$height, numbers=$numbers, cellnumbers=$cellnumbers)"
    }

}


fun main() {

    val test = read("""
        # I am a comment. Hear me roar. Roar.
        name = "denis" # Yeah, you can do this.
        age = 20
        height = 1.75
        numbers = [
          2, 
          5,
          89
        ]
        cellnumbers = ["3518750477", "3253847599"]
    """.trimIndent(), Test::class.java)

    println(test)
}


fun <T> read(source: String, clazz: Class<T>) : T {

    val scanner = Scanner(source)
    val tokens = scanner.scan()

    val parser = Parser(tokens)

    val visitor = Compiler<T>(clazz)
    visitor.visit(parser.parse())

    return visitor.instance
}