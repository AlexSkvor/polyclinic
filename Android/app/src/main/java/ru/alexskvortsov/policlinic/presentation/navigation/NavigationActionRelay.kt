package ru.alexskvortsov.policlinic.presentation.navigation

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class NavigationActionRelay(private val relay: PublishRelay<NavigationAction>) {
    fun pushAction(action: NavigationAction) {
        relay.accept(action)
    }

    fun get(): Observable<NavigationAction> {
        return relay.hide()
    }
}