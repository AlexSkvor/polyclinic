package ru.alexskvortsov.policlinic.ui.fragments.registry

import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.registry.RegistryRecordingPresenter
import ru.alexskvortsov.policlinic.presentation.registry.RegistryRecordingView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment

class RegistryRecordingFragment: BaseMviFragment<RegistryRecordingView, RegistryRecordingPresenter>(), RegistryRecordingView {
    override val layoutRes: Int
        get() = R.layout.registry_recording_fragment

    override fun createPresenter(): RegistryRecordingPresenter = fromScope()
}