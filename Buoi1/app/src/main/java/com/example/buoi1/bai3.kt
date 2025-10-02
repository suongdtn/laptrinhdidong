package com.example.buoi1

fun main() {
    val numbers = listOf(0, 3, 8, 4, 0, 5, 5, 8, 9, 2)
    val setOfNumbers = numbers.toSet()
    println("Original list: $numbers")
    println("Set (unique values): $setOfNumbers")

    val set1 = setOf(1, 2, 3)
    val set2 = mutableSetOf(3, 4, 5)
    println("Intersection: ${set1.intersect(set2)}")
    println("Union: ${set1.union(set2)}")

    val peopleAges = mutableMapOf(
        "Fred" to 30,
        "Ann" to 23
    )
    peopleAges.put("Barbara", 42)
    peopleAges["Joe"] = 51

    peopleAges.forEach { print("${it.key} is ${it.value}, ") }
    println()

    println(peopleAges.map { "${it.key} is ${it.value}" }.joinToString(", "))

    val filteredNames = peopleAges.filter { it.key.length < 4 }
    println("Short names: $filteredNames")

    val words = listOf("about", "acute", "balloon", "best", "brief", "class")
    val filteredWords = words.filter { it.startsWith("b", ignoreCase = true) }
        .shuffled()
        .take(2)
        .sorted()
    println("Words starting with b: $filteredWords")

    val triple: (Int) -> Int = { a: Int -> a * 3 }
    println("5 * 3 = ${triple(5)}")

    var quantity: Int? = null
    println("Quantity = ${quantity ?: 0}")
    quantity = 4
    println("Quantity = ${quantity ?: 0}")
}
