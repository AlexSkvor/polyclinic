package ru.alexskvortsov.policlinic.presentation.navigation

import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo

sealed class NavigationAction {
    data class SignIn(val type: UserAuthInfo.UserType): NavigationAction()

    object Back : NavigationAction()

    data class LoggerFlowAction(val userName: String) : NavigationAction()

    object LogOut : NavigationAction() {
        object AdminNavigation : NavigationAction()
    }
    object OpenDrawing : NavigationAction()
}