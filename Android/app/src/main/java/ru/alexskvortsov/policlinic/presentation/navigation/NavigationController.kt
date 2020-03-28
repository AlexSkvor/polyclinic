package ru.alexskvortsov.policlinic.presentation.navigation

import android.os.Bundle
import androidx.navigation.NavController
import com.pakdd.presentation.system.navigation.NavControllerProvider

class NavigationController(private val navController: NavController) : NavControllerProvider {
    override fun nextScreen(screenId: Int, bundle: Bundle?) {
        if (navController.currentDestination?.getAction(screenId) != null
            || navController.graph.any { navDestination -> navDestination.id == screenId }
        ) {
            navController.navigate(screenId, bundle)
        }
    }

    override fun back() {
        if (navController.currentDestination?.id !in Screens.startScreensIds)
            navController.popBackStack()
    }

    override fun backTo(screenId: Int?) {
        if (screenId != null) navController.popBackStack(screenId, true)
        else while (navController.popBackStack());
    }

    override fun replace(screenId: Int, bundle: Bundle?) {
        navController.popBackStack()
        nextScreen(screenId, bundle)
    }

}