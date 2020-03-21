package ru.alexskvortsov.policlinic.user_generators

private val menFathersNames = listOf(
    "Павлович",
    "Алексеевич",
    "Петрович",
    "Александрович",
    "Васильвевич",
    "Юрьевич",
    "Иванович",
    "Фёдорович",
    "Владиславович",
    "Степанович",
    "Егорович",
    "Андреевич",
    "Сергеевич",
    "Борисович",
    "Михайлович",
    "Дмитриевич"
)

private val womenFathersNames = listOf(
    "Павловна",
    "Алексеевна",
    "Петровна",
    "Александровна",
    "Васильвевна",
    "Юрьевна",
    "Ивановна",
    "Фёдоровна",
    "Владиславовна",
    "Степановна",
    "Егоровна",
    "Андреевна",
    "Сергеевна",
    "Борисовна",
    "Михайловна",
    "Дмитриевна"
)

fun getFathersName(gender: Boolean) =
    if (gender) getMenFathersName()
    else getWomenFathersName()

private fun getMenFathersName(): String = menFathersNames.random()

private fun getWomenFathersName(): String = womenFathersNames.random()