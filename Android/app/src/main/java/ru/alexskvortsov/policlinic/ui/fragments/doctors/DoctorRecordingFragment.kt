package ru.alexskvortsov.policlinic.ui.fragments.doctors

import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorRecordingPresenter
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorRecordingView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment

class DoctorRecordingFragment : BaseMviFragment<DoctorRecordingView, DoctorRecordingPresenter>(), DoctorRecordingView {
    override val layoutRes: Int
        get() = R.layout.doctor_recording_fragment

    override fun createPresenter(): DoctorRecordingPresenter = fromScope()
}