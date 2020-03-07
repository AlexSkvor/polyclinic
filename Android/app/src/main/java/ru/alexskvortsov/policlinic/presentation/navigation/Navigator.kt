package ru.alexskvortsov.policlinic.presentation.navigation

import com.pakdd.presentation.system.navigation.NavControllerProvider
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.BackTo
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

class Navigator(private val navControllerProvider: NavControllerProvider) : ru.terrakok.cicerone.Navigator {

    override fun applyCommands(commands: Array<out Command>?) {
        commands?.forEach { command ->
            when (command) {
                is Back -> back()
                is BackTo -> backTo(command)
                is Forward -> forward(command)
                is Replace -> replace(command)
            }
        }
    }

    private fun back() = navControllerProvider.back()

    private fun backTo(backTo: BackTo) {
        if (backTo.screen != null) {
            navControllerProvider.backTo((backTo.screen as BundleScreen).screenId)
        } else {
            navControllerProvider.backTo(null)
        }
    }

    private fun forward(forward: Forward) =
            (forward.screen as BundleScreen).let { navControllerProvider.nextScreen(it.screenId, it.bundle()) }

    private fun replace(replace: Replace) =
            (replace.screen as BundleScreen).let { navControllerProvider.replace(it.screenId, it.bundle()) }
}