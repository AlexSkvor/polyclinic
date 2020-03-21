package ru.alexskvortsov.policlinic.user_generators

import kotlin.random.Random

fun getPhone(): String{
    var result = "+7999"
    for (i in 1..7) result += Random.nextInt(0, 10)
    return result
}