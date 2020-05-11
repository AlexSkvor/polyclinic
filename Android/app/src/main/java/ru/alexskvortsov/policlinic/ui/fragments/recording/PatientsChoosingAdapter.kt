package ru.alexskvortsov.policlinic.ui.fragments.recording

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_patient.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.strBerthDate
import ru.alexskvortsov.policlinic.ui.utils.delegate.DelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.UserAction

class PatientsChoosingAdapter : DelegateAdapter<PatientEntity>() {

    private val relay: PublishRelay<UserAction<PatientEntity>> = PublishRelay.create()
    override fun getAction(): Observable<UserAction<PatientEntity>> = relay.hide()

    override fun onBind(item: PatientEntity, holder: DelegateViewHolder) = with(holder.itemView) {
        fioField.text = item.fullName
        berthDateField.text = resources.getString(R.string.berthDateFieldValue, item.strBerthDate)
        omsFiled.text = resources.getString(R.string.omsFiledValue, item.omsPoliceNumber)
        setOnClickListener { relay.accept(UserAction.ItemPressed(item)) }
    }

    override fun getLayoutId(): Int = R.layout.item_patient
    override fun isForViewType(items: List<*>, position: Int): Boolean = items[position] is PatientEntity
}