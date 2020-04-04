package ru.alexskvortsov.policlinic.presentation.activity

import com.jakewharton.rxrelay2.BehaviorRelay
import javax.inject.Inject

class UnpackingNotifier @Inject constructor() {
    private val relay: BehaviorRelay<Unit> = BehaviorRelay.create()
    fun notifyUnpacked() = relay.accept(Unit)
    fun unpackResult() = relay.hide().share()
}