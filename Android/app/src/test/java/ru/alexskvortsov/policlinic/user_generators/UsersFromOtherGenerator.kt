package ru.alexskvortsov.policlinic.user_generators

import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserSecondaryEntity


fun UserSecondaryEntity.generatedUser(existsUsers: List<UserEntity>): UserEntity {
    return UserEntity(
        id = userId,
        password = "dev2019",
        login = getLogin(existsUsers.map { it.login })
    )
}

private fun UserSecondaryEntity.getLogin(existsLogins: List<String>): String {
    val nameLetter = fullName.substringBefore(".").last()
    val fathersNameLetter = fullName.substringBeforeLast(".").last()
    val surname = fullName.substringBefore(" ")
    var login = "$nameLetter.$fathersNameLetter.${surname}@user.polyclinic".toLowerCase().toLat()
    var i = 0
    while (login in existsLogins) {
        i++
        login = login.substringBefore("@") + i + "@" + login.substringAfter("@").toLat()
    }
    return login

}

private fun String.toLat(): String {
    return this.asSequence()
        .map {
            when (it) {
                'а' -> "a"
                'б' -> "b"
                'в' -> "v"
                'г' -> "g"
                'д' -> "d"
                'е' -> "e"
                'ё' -> "yo"
                'ж' -> "j"
                'з' -> "z"
                'и' -> "i"
                'й' -> "iy"
                'к' -> "k"
                'л' -> "l"
                'м' -> "m"
                'н' -> "n"
                'о' -> "o"
                'п' -> "p"
                'р' -> "r"
                'с' -> "s"
                'т' -> "t"
                'у' -> "u"
                'ф' -> "f"
                'х' -> "h"
                'ц' -> "c"
                'ч' -> "ch"
                'ш' -> "sh"
                'щ' -> "sh"
                'ъ' -> ""
                'ы' -> "i"
                'ь' -> ""
                'э' -> "e"
                'ю' -> "yu"
                'я' -> "ya"
                else -> it.toString()
            }
        }.joinToString(separator = "") { it }
}
