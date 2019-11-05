package jhb.kotlin.kp

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NullSafetyTest {

    @Test
    fun `using something that might be null in a reference that is not - it is null`() {
        val myPossiblyNullString: String? = null
        Assertions.assertThrows(Exception::class.java) {
            val myNotNullString: String = myPossiblyNullString!! // Don't us the !! (not null assertion) operator
        }
    }

    @Test
    fun `using something that might be null in a reference that is not - it is not null`() {
        val myPossiblyNullString: String? = "hello"
        val myNotNullString: String = myPossiblyNullString!! // Don't us the !! (not null assertion) operator
    }

    @Test
    fun `check for null - conditional`() {
        val myPossiblyNullString: String? = null
        val myNotNullString: String = if (myPossiblyNullString != null) {
            myPossiblyNullString
        } else {
            "default"
        }
    }

    @Test
    fun `check for null - elvis`() {
        val myPossiblyNullString: String? = null
        val myNotNullString: String = myPossiblyNullString ?: "default"
    }

    @Test
    fun `check for null - extension function`() {
        val myPossiblyNullString: String? = null
        var myNotNullString: String = "default"
        myPossiblyNullString?.let { myNotNullString = it }
    }

    @Test
    fun `safe calls`() {
        val myPossiblyNullString: String? = null
        val thePossibleLength: Int? = myPossiblyNullString?.length
    }

}