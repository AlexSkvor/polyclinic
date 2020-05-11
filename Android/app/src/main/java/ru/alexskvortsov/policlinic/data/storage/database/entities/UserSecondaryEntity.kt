package ru.alexskvortsov.policlinic.data.storage.database.entities

import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo

interface UserSecondaryEntity {
    val userId: String
    val fullName: String
    val initialSurnameLetter: String
    val type: UserAuthInfo.UserType
    val realId: String

    fun isPatient(): Boolean {
        return type == UserAuthInfo.UserType.PATIENT
    }
}