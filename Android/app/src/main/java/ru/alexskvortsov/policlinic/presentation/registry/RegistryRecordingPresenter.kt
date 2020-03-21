package ru.alexskvortsov.policlinic.presentation.registry

import ru.alexskvortsov.policlinic.domain.states.registry.RegistryRecordingViewState
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import javax.inject.Inject

class RegistryRecordingPresenter @Inject constructor(

): BaseMviPresenter<RegistryRecordingView, RegistryRecordingViewState>() {
    override fun bindIntents() {
        //TODO("Not yet implemented")
    }
}