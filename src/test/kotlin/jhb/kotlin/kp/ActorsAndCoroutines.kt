package jhb.kotlin.kp

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

suspend fun timed(work: suspend () -> Unit) {
    val start = System.currentTimeMillis()
    work()
    println("Elapsed: ${System.currentTimeMillis() - start}")
}

class CoroutinesBasicsTest {

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


}

/**
 * Coroutine Jobs are things with a lifecycle that need to reach completion.
 * They are hierarchical, so jobs can have children.
 */
class CoroutineStructureTest {

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
    fun `structured concurrency - errors will propagate up and kill the coroutine tree`() = runBlocking {
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

    @Test
    fun `structured concurrency - supervise and dont let child errors kill whole tree`() = runBlocking {
        // Note: Slightly altered copy paste from kotlin docs.
        // To not propagate errors up and down the tree, we need to use a supervisor and handle errors.
        val supervisor = SupervisorJob()
        val scope = CoroutineScope(coroutineContext + supervisor) //Combine the scope of this coroutine with the supervisor.
        with(CoroutineScope(coroutineContext + supervisor)) {
            // launch the first child and we swallow the exception
            launch(CoroutineExceptionHandler { _, _ -> }) {
                println("First child is failing")
                throw AssertionError("First child is cancelled") // Should not be propogated
            }
            // launch the second child
            launch {
                println("Second child is launched and running for a long time")
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    // But cancellation of the supervisor is propagated
                    println("Second child is cancelled because supervisor is cancelled")
                }
            }
        }
        delay(3000)
        println("Cancelling supervisor")
        supervisor.cancel()
    }

    /**
     * Structured concurrency is maintained by the CoroutineScope of each job.
     *
     * If we use something like GlobalScope, we are forcing any coroutines in that scope to be
     * separated from the scope of the original parent, and it will run independently of the parent structure.
     */
    @Test
    fun `coroutine scope - GlobalScope`() = runBlocking {
        val request = launch {
            // it spawns two other jobs, one with GlobalScope
            GlobalScope.launch {
                println("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1: I am not affected by cancellation of the request")
            }
            // and the other inherits the parent context
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // cancel processing of the request
        delay(1000) // delay a second to see what happens
        println("main: Who has survived request cancellation?")
    }
}

/**
 * A CoroutineContext is a context made up of various elements. One of which is the job.
 * The other is a dispatcher. The dispatcher determines which thread or threads a coroutine will use.
 *
 * It is sort of like a processes running on cpu cores. They can context switch and a process can end up running on another core.
 *
 * Threads are like the cores, and coroutines are like the processes.
 *
 * But, we can control which threads our coroutines are allowed to execute on.
 */
class CoroutineContextTest {

    val singleThreadContext = newSingleThreadContext("MySingleThreadContext")

    @Test
    fun `dispatchers on different threads`() = runBlocking {
        val jobs = listOf(launch {
            // context of the parent, main runBlocking coroutine
            println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
        },
            launch(Dispatchers.Unconfined) {
                // not confined -- will work with main thread
                println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
            },
            launch(Dispatchers.Default) {
                // will get dispatched to DefaultDispatcher
                println("Default               : I'm working in thread ${Thread.currentThread().name}")
            },
            launch(singleThreadContext) {
                // will get its own new thread
                println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
            })
        jobs.joinAll()
    }

    @Test
    fun `combining contexts`() {
        runBlocking {
            launch(Dispatchers.Default + CoroutineName("my custom name")) {
                println("I'm working in thread ${Thread.currentThread().name}")
            }
        }
    }


}


