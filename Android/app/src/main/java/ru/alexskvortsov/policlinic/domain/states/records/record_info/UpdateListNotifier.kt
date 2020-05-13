package ru.alexskvortsov.policlinic.domain.states.records.record_info

import com.jakewharton.rxrelay2.PublishRelay

class UpdateListNotifier {

    private val relay = PublishRelay.create<Unit>()
    fun actions() = relay.hide()
    fun reload() = relay.accept(Unit)

}