package ru.alexskvortsov.policlinic.user_generators

private val menSurnames = listOf(
    "Смирнов",
    "Иванов",
    "Кузнецов",
    "Соколов",
    "Попов",
    "Лебедев",
    "Козлов",
    "Новиков",
    "Морозов",
    "Петров",
    "Волков",
    "Соловьёв",
    "Васильев",
    "Зайцев",
    "Павлов",
    "Виноградов"
)

private val womenSurnames = listOf(
    "Смирнова",
    "Иванова",
    "Кузнецова",
    "Соколова",
    "Попова",
    "Лебедева",
    "Козлова",
    "Новикова",
    "Морозова",
    "Петрова",
    "Волкова",
    "Соловьёва",
    "Васильева",
    "Зайцева",
    "Павлова",
    "Виноградова"
)

fun getSurname(gender: Boolean) =
    if (gender) getMenSurname()
    else getWomenSurname()

private fun getMenSurname(): String = menSurnames.random()

private fun getWomenSurname(): String = womenSurnames.random()