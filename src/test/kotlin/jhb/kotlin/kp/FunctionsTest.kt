package jhb.kotlin.kp

import org.junit.jupiter.api.Test

class FunctionsTest {

    @Test
    fun `extension functions`() {
        val result = 1.add(2)
    }

    @Test
    fun `infix function`() {
        val result = 1 multipliedBy 2
    }

    @Test
    fun `higher order function`() {
        val originalCollection = listOf(1, 2, 3)
        println(originalCollection.myMap { it * 2 })
    }

    @Test
    fun `lambda`() {
        val printString: StringSideEffect = { a: String ->
            println(a)
        }
        printString("hello")
    }

    @Test
    fun `anonymous`(){
        val printString = fun(a: String){
            println(a)
        }
        printString("hello")
    }
}

typealias StringSideEffect = (String) -> Unit

fun Int.add(other: Int): Int {
    return this + other
}

infix fun Int.multipliedBy(other: Int): Int {
    return this * other
}

fun <T> Collection<T>.myMap(mappingFunction: (element: T) -> T): Collection<T> {
    // A bad implementation of map
    val result = mutableListOf<T>()
    this.forEach {
        result.add(mappingFunction(it))
    }
    return result
}