package ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_procedure.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity
import ru.alexskvortsov.policlinic.ui.utils.delegate.DelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.UserAction

class ProceduresAdapter(
    val isChosen: (ProcedureEntity) -> Boolean
) : DelegateAdapter<ProcedureEntity>() {

    private val relay: PublishRelay<UserAction<ProcedureEntity>> = PublishRelay.create()
    override fun getAction(): Observable<UserAction<ProcedureEntity>> = relay.hide()

    override fun onBind(item: ProcedureEntity, holder: DelegateViewHolder) = with(holder.itemView) {

        procedureRadioButton.isActivated = isChosen(item)
        procedureName.text = item.name

        setOnClickListener {
            if (isChosen(item)) relay.accept(UserAction.ItemPressed(item))
            else relay.accept(UserAction.ItemDeleted(item))
        }
    }

    override fun getLayoutId(): Int = R.layout.item_procedure
    override fun isForViewType(items: List<*>, position: Int): Boolean = items[position] is ProcedureEntity
}