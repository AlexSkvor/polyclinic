package ru.alexskvortsov.policlinic.ui.fragments.registry

import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.registry.RegistryProfilePresenter
import ru.alexskvortsov.policlinic.presentation.registry.RegistryProfileView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment

class RegistryProfileFragment : BaseMviFragment<RegistryProfileView, RegistryProfilePresenter>(), RegistryProfileView {
    override val layoutRes: Int
        get() = R.layout.registry_profile_fragment

    override fun createPresenter(): RegistryProfilePresenter = fromScope()
}