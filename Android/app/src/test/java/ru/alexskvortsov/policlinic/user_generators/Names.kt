package ru.alexskvortsov.policlinic.user_generators

private val menNames = listOf(
    "Павел",
    "Алексей",
    "Пётр",
    "Александр",
    "Василий",
    "Юрий",
    "Иван",
    "Фёдор",
    "Владислав",
    "Степан",
    "Егор",
    "Андрей",
    "Сергей",
    "Борис",
    "Михаил",
    "Дмитрий"
)

private val womenNames = listOf(
    "Софья",
    "Анастасия",
    "Мария",
    "Анна",
    "Виктория",
    "Полина",
    "Елизавета",
    "Екатерина",
    "Ксения",
    "Валерия",
    "Александра",
    "Вероника",
    "Алиса",
    "Елена",
    "Маргарита",
    "Кристина"
)

fun getName(gender: Boolean) =
    if (gender) getMenName()
    else getWomenName()

private fun getMenName(): String = menNames.random()

private fun getWomenName(): String = womenNames.random()