package ru.alexskvortsov.policlinic.presentation.records.list

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.records.list.RecordsViewState

interface RecordsView : MvpView {

    fun reloadIntent(): Observable<Unit>
    fun typeChangeIntent(): Observable<RecordsViewState.ListType>

    fun render(state: RecordsViewState)
}