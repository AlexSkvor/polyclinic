package ru.alexskvortsov.policlinic.domain.states.auth

data class ChangePasswordUser(
    val userId: String,
    val currentPassword: String,
    val newPassword: String,
    val retryNewPassword: String
)