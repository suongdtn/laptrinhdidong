package com.example.buoi1

fun main() {
    println("Hello, world!")
    println("This is the text to print!")

    val age = 5
    val name = "Rover"
    var roll = 6

    println("You are already ${age}!")
    println("You are already ${age} years old, ${name}!")

    printHello()
    printBorder("=", 10)

    val diceResult = rollDice()
    println("You rolled a $diceResult")

    if (diceResult > 3) {
        println("Your roll is greater than 3")
    } else {
        println("Your roll is 3 or less")
    }

    when (diceResult) {
        1 -> println("So sorry! You rolled a 1.")
        2 -> println("Try again, you rolled a 2.")
        3 -> println("Not bad, you rolled a 3.")
        4 -> println("Great, you rolled a 4!")
        5 -> println("Awesome, you rolled a 5!")
        6 -> println("Jackpot! You rolled a 6!")
    }

    val myFirstDice = Dice(6)
    println("My dice rolled: ${myFirstDice.roll()}")
}

fun printHello() {
    println("Hello Kotlin")
}

fun printBorder(border: String, timesToRepeat: Int) {
    repeat(timesToRepeat) {
        print(border)
    }
    println()
}

fun rollDice(): Int {
    return (1..6).random()
}

class Dice(val numSides: Int) {
    fun roll(): Int {
        return (1..numSides).random()
    }
}