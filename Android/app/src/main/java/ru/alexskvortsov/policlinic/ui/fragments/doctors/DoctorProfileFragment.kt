package ru.alexskvortsov.policlinic.ui.fragments.doctors

import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorProfilePresenter
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorProfileView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment

class DoctorProfileFragment : BaseMviFragment<DoctorProfileView, DoctorProfilePresenter>(), DoctorProfileView {
    override val layoutRes: Int
        get() = R.layout.doctor_profile_fragment

    override fun createPresenter(): DoctorProfilePresenter = fromScope()
}