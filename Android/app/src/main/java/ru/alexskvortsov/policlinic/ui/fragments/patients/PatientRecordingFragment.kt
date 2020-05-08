package ru.alexskvortsov.policlinic.ui.fragments.patients

import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.patient.PatientRecordingPresenter
import ru.alexskvortsov.policlinic.presentation.patient.PatientRecordingView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment

class PatientRecordingFragment: BaseMviFragment<PatientRecordingView, PatientRecordingPresenter>(), PatientRecordingView {
    override val layoutRes: Int
        get() = R.layout.patient_recording_fragment

    override fun createPresenter(): PatientRecordingPresenter = fromScope()
}