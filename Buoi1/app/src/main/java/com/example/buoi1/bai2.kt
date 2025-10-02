package com.example.buoi1

import kotlin.math.PI

abstract class Dwelling {
    abstract val buildingMaterial: String
    abstract fun floorArea(): Double
    open fun capacity() = 4
    fun hasRoom() = capacity() > 0
}

open class RoundHut(val residents: Int) : Dwelling() {
    override val buildingMaterial = "Straw"
    override fun floorArea(): Double {
        return PI * 2.0 * 2.0
    }
}

class SquareCabin(val residents: Int) : Dwelling() {
    override val buildingMaterial = "Wood"
    override fun floorArea(): Double {
        return 3.0 * 3.0
    }
}

class bai2 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val squareCabin = SquareCabin(4)
            with(squareCabin) {
                println("Capacity: ${capacity()}")
                println("Material: $buildingMaterial")
                println("Has room? ${hasRoom()}")
            }

            val numbers = listOf(1, 2, 3, 4, 5, 6)
            println(numbers.size)
            println(numbers[0])
            println(listOf("red", "blue", "green").reversed())

            val entrees = mutableListOf<String>()
            entrees.add("spaghetti")
            entrees[0] = "lasagna"
            entrees.remove("lasagna")
            println(entrees)

            for (element in numbers) {
                println(element)
            }

            var index = 0
            while (index < numbers.size) {
                println(numbers[index])
                index++
            }

            val name = "Android"
            println(name.length)

            val number = 10
            println("$number people")

            val groups = 5
            println("${number * groups} people")

            var a = 5
            var b = 3
            a += b
            a -= b
            a *= b
            a /= b
            println(a)

            println(kotlin.math.PI * 2 * 2)

            val stringInTextField = "Hello World".toString()
            println(stringInTextField)

            addToppings("cheese", "tomato", "basil")
        }

        fun addToppings(vararg toppings: String) {
            for (topping in toppings) {
                println("Topping: $topping")
            }
        }
    }
}
