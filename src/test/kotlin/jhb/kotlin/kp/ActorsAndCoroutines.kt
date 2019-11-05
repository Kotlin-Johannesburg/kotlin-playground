package jhb.kotlin.kp

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

suspend fun timed(work: suspend () -> Unit) {
    val start = System.currentTimeMillis()
    work()
    println("Elapsed: ${System.currentTimeMillis() - start}")
}


class CoroutinesTest {

    @Test
    fun `coroutines are concurrent`() = runBlocking {
        launch {
            delay(2000)
            println("I will execute last")
        }
        launch {
            delay(1000)
            println("I should execute in the middle")
        }
        println("I should execute immediately")
    }

    @Test
    fun `getting results from a coroutine`() = runBlocking {
        timed {
            val deferredResult1 = async {
                delay(2000)
                1
            }
            val deferredResult2 = async {
                delay(1000)
                2
            }
            println(println(deferredResult1.await() + deferredResult2.await()))
        }
    }

    @Test
    fun `coroutines are light weight`() = runBlocking {
        val jobs = List(100_000) {
            launch {
                delay(1000)
                print(".")
            }
        }
        delay(2000)
        println()
        println("All done")
    }

    @Test
    fun `structured concurrency - if a parent dies, its children are not left dangling`() = runBlocking {
        val parentJob = launch {
            println("Parent started")
            launch {
                println("Child job 1 started")
                delay(3000)
                println("Child job 1 done")
            }
            launch {
                println("Child job 2 started")
                delay(2000)
                println("Child job 2 is done")
            }
        }
        delay(1000)
        println("Cancelling the parent")
        parentJob.cancel()
    }

    @Test
    fun `structured concurrency - errors will propagate up`() = runBlocking {
        val parentJob = launch {
            println("Parent started")
            launch {
                println("Child job 1 started")
                delay(3000)
                println("I should not happen because my sibling will freak out")
            }
            launch {
                println("Child job 2 started, but I am going to freak out")
                delay(2000)
                throw Exception("freak out")
            }
        }
        parentJob.join()
        println("I should not happen because one of the children freaked out")
    }
}


