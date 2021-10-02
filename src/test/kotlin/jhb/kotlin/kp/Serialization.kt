package jhb.kotlin.kp

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Serialization {

    @OptIn(InternalSerializationApi::class)
    val json = Json {
        encodeDefaults=true
        isLenient = true
        serializersModule = SerializersModule {

        }
    }

    @Test
    fun `serialise deserialise sealed class sub types`() {
        val thing1 =MySubThing1("Original")
        val serialised = json.encodeToString(
            MyThing.serializer(),
            thing1
        )
        println(serialised)
        val thing1Deserialised: MyThing = json.decodeFromString(
            serialised
        )
        println(thing1Deserialised)
    }


    @Test
    fun `serialise deserialise internal sealed class`() {
        val parent = ParentThingWithSealedComponent(
            MySubThing2("Original")
        )
        val serialised = json.encodeToString(
            parent
        )
        println(serialised)
        val parentDeserialised: ParentThingWithSealedComponent = json.decodeFromString(
            serialised
        )
        println(parentDeserialised)
        Assertions.assertEquals(parent, parentDeserialised)
    }

    @Test
    fun `serialise deserialise arbitrary implementation of an interface`(){
        val original: Foo = Bar("something")
        val serialised = json.encodeToString(original)

        val deserialized: Foo = json.decodeFromString(serialised)

        Assertions.assertEquals(original, deserialized)
    }


}

interface Foo

@Serializable
@SerialName("Bar")
data class Bar(
    val property: String
): Foo

@Serializable
data class ParentThingWithSealedComponent(
    val myThing: MyThing
)

@Serializable
sealed class MyThing {
    abstract val foo: String
}

@Serializable
@SerialName("Thing1")
data class MySubThing1(val name: String) : MyThing() {
    override val foo = "Bar1"
}

@Serializable
@SerialName("Thing2")
data class MySubThing2(val name: String) : MyThing() {
    override val foo = "Bar2"
}