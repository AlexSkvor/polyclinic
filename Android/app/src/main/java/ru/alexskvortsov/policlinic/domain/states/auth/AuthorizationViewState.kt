package ru.alexskvortsov.policlinic.domain.states.auth

data class AuthorizationViewState(
    val userList: List<UserAuthInfo> = emptyList(),
    val userListType: UserAuthInfo.UserType = UserAuthInfo.UserType.PATIENT,
    val emptyListVisible: Boolean = false,
    val updateListUsers: Boolean = false,
    val authInfo: AuthElementInfo? = null
)

enum class FieldType {
    LOGIN_PASSWORD, OLD_PASSWORD, CHANGE_PASSWORD, REPEAT_CHANGE_PASSWORD
}

enum class PasswordState {
    EMPTY, INVALID, VALID, NOT_EQUAL
}

data class AuthElementInfo(val fieldType: FieldType, val passwordState: PasswordState)