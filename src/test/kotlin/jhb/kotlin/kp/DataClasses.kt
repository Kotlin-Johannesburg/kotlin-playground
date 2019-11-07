package jhb.kotlin.kp

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Nice and concise and immutable (with vals)
 */
data class Person(
    val firstName: String,
    val lastName: String
)

class DataClassesTest {

    @Test
    fun `equality and hash work on data not instance`() {
        val siya1 = Person("Siya", "Kolisi")
        val siya2 = Person("Siya", "Kolisi")
        Assertions.assertEquals(siya1, siya2)
        Assertions.assertEquals(siya1.hashCode(), siya2.hashCode())
    }

    @Test
    fun `nice printing`() {
        val sarah = Person("Sarah", "Smith")
        println(sarah)
    }

    @Test
    fun `nice copying and modification of immutable DTOs`(){
        val sarah = Person("Sarah", "Simth")
        val sarahEdited = sarah.copy(lastName="Smith")
        Assertions.assertNotSame(sarah, sarahEdited)
    }

}