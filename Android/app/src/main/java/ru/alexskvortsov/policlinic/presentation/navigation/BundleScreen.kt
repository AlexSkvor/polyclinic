package ru.alexskvortsov.policlinic.presentation.navigation

import android.os.Bundle
import ru.terrakok.cicerone.Screen

abstract class BundleScreen : Screen() {
    abstract val screenId: Int
    open fun bundle(): Bundle? = null
}