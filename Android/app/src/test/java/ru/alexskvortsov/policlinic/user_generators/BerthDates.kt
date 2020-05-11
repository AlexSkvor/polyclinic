package ru.alexskvortsov.policlinic.user_generators

import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.formatterDB
import kotlin.random.Random

fun getBerthDate(): LocalDateTime = LocalDateTime.parse(randomDateString(), formatterDB)

private fun randomDateString(): String {
    val year = Random.nextInt(1980, 2000).toString()
    val day = Random.nextInt(1, 29).toTwoDigitsString()
    val month = Random.nextInt(1, 13).toTwoDigitsString()
    return "$month.$day.$year 00:00:00"
}

private fun Int.toTwoDigitsString(): String {
    return if (this < 10) "0$this"
    else this.toString()
}