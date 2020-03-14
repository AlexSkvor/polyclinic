package ru.alexskvortsov.policlinic.domain.states.auth

import ru.alexskvortsov.policlinic.data.storage.database.entities.UserSecondaryEntity
import ru.alexskvortsov.policlinic.domain.utils.RecyclerItem

data class UserAuthInfo constructor(
    val id: String,
    override val fullName: String,
    override val initialSurnameLetter: String,
    val password: String,
    override val type: UserType,
    override val realId: String,
    val userUseToApp: Int = NOT_IN_HISTORY
) : UserSecondaryEntity, RecyclerItem {
    enum class UserType(val code: Int) { DOCTOR(0), REGISTRY(1), PATIENT(2) }

    override val userId: String
        get() = id
}

const val NOT_IN_HISTORY = -1