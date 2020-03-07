package ru.alexskvortsov.policlinic.domain.states.activity

data class AppViewState(
    val toast: String = "",
    val snackBar: String = "",
    val progress: Boolean = false,
    val render: Render = Render.NONE
) {
    enum class Render {
        TOAST, SNACK_BAR, PROGRESS, NONE, RESTORE
    }
}