package ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation

import android.os.Bundle
import kotlinx.android.synthetic.main.consultations_detail_fragment.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.states.records.record_info.ConsultationInfoViewState
import ru.alexskvortsov.policlinic.toothpick.DI
import ru.alexskvortsov.policlinic.visible
import ru.alexskvortsov.policlinic.withArgument

class BigPastConsultationFragment : BaseConsultationInfoDialogFragment() {

    companion object {
        fun newInstance(parentScope: String? = null): BigPastConsultationFragment =
            BigPastConsultationFragment().apply { setParentScopeName(parentScope) }

        const val TAG = "BigPastConsultationFragment"
    }

    override fun childRender(state: ConsultationInfoViewState) {
        inflatedView.kostyl.visible = false

        inflatedView.cancelConsultationButton.visible = false
        inflatedView.startConsultationButton.visible = false
        inflatedView.undoButton.visible = false
        inflatedView.saveButton.visible = false

        inflatedView.factRecordInfo.visible = !state.record.cancelled

        inflatedView.titleFactRecord.isEnabled = !state.record.cancelled
        inflatedView.titleFactRecord.text = if (state.record.cancelled) getString(R.string.notStarted)
        else getString(R.string.titleFactConsultation)
    }

    override fun setParentScopeName(parentScopeName: String?) {
        arguments = Bundle().withArgument(PARENT_SCOPE_NAME, parentScopeName ?: DI.APP_SCOPE)
    }
}