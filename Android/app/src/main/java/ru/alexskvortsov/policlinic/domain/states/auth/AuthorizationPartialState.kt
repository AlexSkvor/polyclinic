package ru.alexskvortsov.policlinic.domain.states.auth

sealed class AuthorizationPartialState(private val loggerMessage: String) {

    data class LoadingUsers(val userList: List<UserAuthInfo>) : AuthorizationPartialState("LoadingUsers $userList")
    data class UserSuccessPassword(val userId: String, val type: UserAuthInfo.UserType) : AuthorizationPartialState("UserSuccessPassword $userId")
    data class SearchUserList(val userList: List<UserAuthInfo>) : AuthorizationPartialState("SearchUserList $userList")
    data class UserChangePassword(val userList: List<UserAuthInfo>) : AuthorizationPartialState("UserChangePassword $userList")
    data class UserType(val type: UserAuthInfo.UserType) : AuthorizationPartialState("UserType $type")

    object CancelChangePassword : AuthorizationPartialState("CancelChangePassword")
    object UserFailedLogin : AuthorizationPartialState("UserFailedLogin")

    object ErrorChangeWrongCurrentPassword : AuthorizationPartialState("ErrorChangeWrongCurrentPassword")
    object ErrorChangeNewPasswordNoNumbers : AuthorizationPartialState("ErrorChangeNewPasswordNoNumbers")
    object ErrorChangeNewPasswordNoLetters : AuthorizationPartialState("ErrorChangeNewPasswordNoLetters")
    object ErrorChangeNewPasswordOnlyNumbersAndLettersAllowed : AuthorizationPartialState("ErrorChangeNewPasswordOnlyNumbersAndLettersAllowed")
    object ChangeNewAndOldPasswordsNotEquals : AuthorizationPartialState("ChangeNewAndOldPasswordsNotEquals")

    fun partial() = this

    override fun toString(): String {
        return loggerMessage
    }

}