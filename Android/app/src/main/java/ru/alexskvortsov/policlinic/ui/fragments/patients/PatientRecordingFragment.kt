package ru.alexskvortsov.policlinic.ui.fragments.patients

import android.os.Bundle
import android.view.View
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.synthetic.main.patient_recording_fragment.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.patient.PatientRecordingPresenter
import ru.alexskvortsov.policlinic.presentation.patient.PatientRecordingView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment
import ru.alexskvortsov.policlinic.ui.fragments.recording.RecordingDialogFragment
//TODO make it common
class PatientRecordingFragment : BaseMviFragment<PatientRecordingView, PatientRecordingPresenter>(), PatientRecordingView {
    override val layoutRes: Int
        get() = R.layout.patient_recording_fragment


    override fun createPresenter(): PatientRecordingPresenter = fromScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startRecordingButton.setOnClickListener { startRecording() }
    }

    private val recordAddedRelay = PublishRelay.create<String>()
    private fun startRecording() {
        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(RECORDING_DIALOG_TAG)
        if (fragment == null) {
            val recordingFragment = RecordingDialogFragment.newInstance(scope.name.toString()) {
                recordAddedRelay.accept(it)
            }
            recordingFragment.show(requireActivity().supportFragmentManager, RECORDING_DIALOG_TAG)
            requireActivity().supportFragmentManager.executePendingTransactions()
        }
    }

    companion object {
        const val RECORDING_DIALOG_TAG = "RECORDING_DIALOG_TAG"
    }
}