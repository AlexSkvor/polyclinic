package ru.alexskvortsov.policlinic.ui.fragments.recording

import android.os.Bundle
import android.view.View
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.synthetic.main.records_fragment.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.recording.RecordsPresenter
import ru.alexskvortsov.policlinic.presentation.recording.RecordsView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment

class RecordsFragment : BaseMviFragment<RecordsView, RecordsPresenter>(),
    RecordsView {
    override val layoutRes: Int
        get() = R.layout.records_fragment

    override fun createPresenter(): RecordsPresenter = fromScope()

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
            recordingFragment.show(requireActivity().supportFragmentManager,
                RECORDING_DIALOG_TAG
            )
            requireActivity().supportFragmentManager.executePendingTransactions()
        }
    }

    companion object {
        const val RECORDING_DIALOG_TAG = "RECORDING_DIALOG_TAG"
    }
}