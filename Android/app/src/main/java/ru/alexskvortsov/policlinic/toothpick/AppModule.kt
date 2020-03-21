package ru.alexskvortsov.policlinic.toothpick

import com.jakewharton.rxrelay2.PublishRelay
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationActionRelay
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import timber.log.Timber
import toothpick.config.Module

class AppModule: Module() {
    init {
        bind(SystemMessage::class.java).toInstance(SystemMessage())

        Timber.d("cicerone global router")
        val cicerone = Cicerone.create()
        bind(ru.terrakok.cicerone.Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)
        Timber.d("initAuth navigation action")
        bind(NavigationActionRelay::class.java).toInstance(NavigationActionRelay(PublishRelay.create()))
    }
}