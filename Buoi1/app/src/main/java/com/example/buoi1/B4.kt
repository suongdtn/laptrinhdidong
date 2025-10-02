package com.example.buoi1
import kotlinx.coroutines.*

object B4 {
    suspend fun getValue(): Double {
        delay(1000L)
        return Math.random()
    }

    suspend fun processValue() {
        val value = getValue()
        println("Processed value: ${value * 2}")
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job: Job = GlobalScope.launch {
            val output = getValue()
            println("GlobalScope output: $output")
        }

        job.cancel()

        val output = getValue()
        println("Blocking output: $output")

        val deferred = async { getValue() }
        println("Async output: ${deferred.await()}")

        val direction = Direction.NORTH
        when (direction) {
            Direction.NORTH -> println("Going north")
            Direction.SOUTH -> println("Going south")
            Direction.WEST -> println("Going west")
            Direction.EAST -> println("Going east")
        }

        try {
            throw Exception("Test exception")
        } catch (exception: Exception) {
            println("Caught exception: ${exception.message}")
        }
    }

    object DataProviderManager

    enum class Direction {
        NORTH, SOUTH, WEST, EAST
    }
}
