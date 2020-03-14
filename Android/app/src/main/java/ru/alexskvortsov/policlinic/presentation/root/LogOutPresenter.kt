package ru.alexskvortsov.policlinic.presentation.root

import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationAction
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationActionRelay
import javax.inject.Inject

class LogOutPresenter @Inject constructor(
    private val navigation: NavigationActionRelay,
    private val prefs: AppPrefs
) : BaseMviPresenter<LogOutView, Any>() {

    override fun bindIntents() {
        intent(LogOutView::logOutIntent).subscribe {
            prefsLogout()
            visualLogout()
        }.bind()

        intent(LogOutView::initLogout).subscribe { prefsLogout() }.bind()
    }

    private fun prefsLogout() {
        prefs.localAuthorisation = false
    }

    private fun visualLogout() {
        navigation.pushAction(NavigationAction.LogOut)
    }
}