package ru.alexskvortsov.policlinic.presentation.navigation

sealed class NavigationAction {
    object SignIn : NavigationAction() {
        object Authorization : NavigationAction()
        data class SyncAction(val withBackStack: Boolean = true) : NavigationAction()
        object SettingConnectionAction : NavigationAction()
    }

    object Back : NavigationAction()

    data class LoggerFlowAction(val userName: String) : NavigationAction()

    object LogOut : NavigationAction() {
        object AdminNavigation : NavigationAction()
    }
    object OpenDrawing : NavigationAction()
}