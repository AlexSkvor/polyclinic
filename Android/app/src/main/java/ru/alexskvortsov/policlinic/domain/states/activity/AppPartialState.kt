package ru.alexskvortsov.policlinic.domain.states.activity

import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo

sealed class AppPartialState(private val logMessage: String) {

    data class SignIn(val type: UserAuthInfo.UserType) : AppPartialState("SignIn $type")
    object LogOut : AppPartialState("LogOut")

    object Restore : AppPartialState("Restore")
    object Refresh : AppPartialState("Refresh")
    data class Toast(val message: String) : AppPartialState("Toast $message")
    data class Snackbar(val message: String) : AppPartialState("Snackbar $message")
    data class Progress(val value: Boolean) : AppPartialState("Progress $value")

    override fun toString(): String = logMessage

}