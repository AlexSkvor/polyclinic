package ru.alexskvortsov.policlinic.ui.fragments.doctors

import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorAppointmentPresenter
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorAppointmentView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment

class DoctorAppointmentFragment : BaseMviFragment<DoctorAppointmentView, DoctorAppointmentPresenter>(), DoctorAppointmentView {
    override val layoutRes: Int
        get() = R.layout.doctor_appointment_fragment

    override fun createPresenter(): DoctorAppointmentPresenter = fromScope()
}