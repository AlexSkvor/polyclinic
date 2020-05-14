package ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation

import android.os.Bundle
import kotlinx.android.synthetic.main.consultations_detail_fragment.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.states.records.record_info.ConsultationInfoViewState
import ru.alexskvortsov.policlinic.toothpick.DI
import ru.alexskvortsov.policlinic.visible
import ru.alexskvortsov.policlinic.withArgument

class SmallPastConsultationFragment : BaseConsultationInfoDialogFragment() {

    companion object {
        fun newInstance(parentScope: String? = null): SmallPastConsultationFragment =
            SmallPastConsultationFragment().apply { setParentScopeName(parentScope) }

        const val TAG = "SmallPastConsultationFragment"
    }

    override fun childRender(state: ConsultationInfoViewState) {
        inflatedView.kostyl.visible = false

        inflatedView.cancelConsultationButton.visible = false
        inflatedView.startConsultationButton.visible = false
        inflatedView.undoButton.visible = false
        inflatedView.saveButton.visible = false
        inflatedView.factRecordInfo.visible = false

        inflatedView.titleFactRecord.isEnabled = false
        inflatedView.titleFactRecord.text = getString(R.string.notPermitted)
    }

    override fun setParentScopeName(parentScopeName: String?) {
        arguments = Bundle().withArgument(PARENT_SCOPE_NAME, parentScopeName ?: DI.APP_SCOPE)
    }
}