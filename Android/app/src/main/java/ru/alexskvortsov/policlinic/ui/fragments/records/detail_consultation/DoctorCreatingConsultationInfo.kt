package ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation

import android.os.Bundle
import kotlinx.android.synthetic.main.consultations_detail_fragment.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.states.records.record_info.ConsultationInfoViewState
import ru.alexskvortsov.policlinic.getColor
import ru.alexskvortsov.policlinic.toothpick.DI
import ru.alexskvortsov.policlinic.visible
import ru.alexskvortsov.policlinic.withArgument

class DoctorCreatingConsultationInfo : BaseConsultationInfoDialogFragment() {

    companion object {
        fun newInstance(parentScope: String? = null): DoctorCreatingConsultationInfo =
            DoctorCreatingConsultationInfo().apply { setParentScopeName(parentScope) }

        const val TAG = "DoctorCreatingConsultationInfo"
    }

    override fun childRender(state: ConsultationInfoViewState) {
        inflatedView.startConsultationButton.visible = !state.started
        inflatedView.cancelConsultationButton.visible = !state.started
        inflatedView.undoButton.visible = state.started
        inflatedView.saveButton.visible = state.started
        inflatedView.factRecordInfo.visible = state.started

        inflatedView.titleFactRecord.isEnabled = state.started
        inflatedView.additionalNotesFactRecord.isEnabled = state.started
        inflatedView.saveButton.isEnabled = state.canSave

        inflatedView.saveButton.setTextColor(getColor(if (state.canSave) R.color.colorAccent else R.color.attributeNameColor))

        inflatedView.titleFactRecord.text = if (!state.started) getString(R.string.notStarted)
        else getString(R.string.titleFactConsultation)
    }

    override fun setParentScopeName(parentScopeName: String?) {
        arguments = Bundle().withArgument(PARENT_SCOPE_NAME, parentScopeName ?: DI.APP_SCOPE)
    }
}