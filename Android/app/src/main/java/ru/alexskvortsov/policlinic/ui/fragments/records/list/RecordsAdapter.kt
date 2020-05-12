package ru.alexskvortsov.policlinic.ui.fragments.records.list

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_consultation.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.states.records.list.Record
import ru.alexskvortsov.policlinic.formatterUiDateTime
import ru.alexskvortsov.policlinic.getColor
import ru.alexskvortsov.policlinic.ui.utils.delegate.DelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.UserAction

class RecordsAdapter : DelegateAdapter<Record>() {

    private val relay: PublishRelay<UserAction<Record>> = PublishRelay.create()
    override fun getAction(): Observable<UserAction<Record>> = relay.hide()

    override fun onBind(item: Record, holder: DelegateViewHolder) = with(holder.itemView) {
        val competenceText = if (item.cancelled) resources.getString(R.string.competenceConsultationInfoCancelled, item.competenceEntity.name)
        else resources.getString(R.string.competenceConsultationInfo, item.competenceEntity.name)
        competenceField.text = competenceText

        competenceField.setTextColor(getColor(if (item.cancelled) R.color.palette_red else R.color.colorAccent))

        patientField.text = resources.getString(R.string.patientConsultationInfo, item.patientEntity.fullName)
        doctorField.text = resources.getString(R.string.doctorConsultationInfo, item.doctorEntity.fullName)
        val time = item.startTimeFact ?: item.startTimePlan
        dateTimeField.text = resources.getString(R.string.dateTimeConsultationInfo, time.format(formatterUiDateTime))
        creatorFieldField.text = resources.getString(R.string.creatorConsultationInfo, item.createdUserEntity.login)
        setOnClickListener { relay.accept(UserAction.ItemPressed(item)) }
    }

    override fun getLayoutId(): Int = R.layout.item_consultation
    override fun isForViewType(items: List<*>, position: Int): Boolean = items[position] is Record
}