package ru.alexskvortsov.policlinic.ui.fragments.recording

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_time.view.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.formatterUiTime
import ru.alexskvortsov.policlinic.getColor
import ru.alexskvortsov.policlinic.ui.utils.delegate.DelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.UserAction

class TimeChoosingAdapter(
    val chosenTime: () -> LocalDateTime?
) : DelegateAdapter<LocalDateTime>() {

    private val relay: PublishRelay<UserAction<LocalDateTime>> = PublishRelay.create()
    override fun getAction(): Observable<UserAction<LocalDateTime>> = relay.hide()

    override fun onBind(item: LocalDateTime, holder: DelegateViewHolder) = with(holder.itemView) {
        durationField.text = resources.getString(R.string.durationItemText, item.format(formatterUiTime))
        if (chosenTime() == item) setBackgroundColor(getColor(R.color.colorLightGreyBlue))
        else setBackgroundColor(getColor(R.color.transparent))
        setOnClickListener { relay.accept(UserAction.ItemPressed(item)) }
    }

    override fun getLayoutId(): Int = R.layout.item_time
    override fun isForViewType(items: List<*>, position: Int): Boolean = items[position] is LocalDateTime
}