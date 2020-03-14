package ru.alexskvortsov.policlinic.domain.states.auth

data class InputUserPassword(
    val userId: String,
    val password: String
)