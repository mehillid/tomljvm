import com.github.unldenis.tomljvm.TomlJvm
import kotlin.test.Test

data class Test(
    var name : String? = null,
    private var age : Int? = null,
    private var height : Float? = null,
    private var numbers : List<Int>? = null,
    private var cellnumbers : List<String>? = null,
    private var db : DB? = null,
    private var sqlite : Sqlite? = null
)

data class DB(var type : String? = null)
data class Sqlite(var path : String? = null, var name_file : String? = null)


class TomljvmTest {
    @Test
    fun anotherTest() {

        val test = TomlJvm.read("""
        # I am a comment. Hear me roar. Roar.
        name = "denis" # Yeah, you can do this.
        age = 20
        height = 1.75
        numbers = [
          2, 
          5,
          89
        ]
        cellnumbers = ["4334335433", "3253847599"]
        
        [db]
        type = "sqlite"
        
        [sqlite]
        path = "/easyml/"
        name_file = "data"

    """.trimIndent(), Test::class.java)

        println(test)
    }

}