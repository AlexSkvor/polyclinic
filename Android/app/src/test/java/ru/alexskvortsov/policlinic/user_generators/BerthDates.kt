package ru.alexskvortsov.policlinic.user_generators

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.random.Random


fun getBerthDate(): LocalDateTime = LocalDateTime.parse(randomDateString(), formatter)

private val formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm:ss")

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