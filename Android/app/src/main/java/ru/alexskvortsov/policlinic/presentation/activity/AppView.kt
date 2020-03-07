package ru.alexskvortsov.policlinic.presentation.activity

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.activity.AppViewState

interface AppView : MvpView {

    fun restore(): Observable<Unit>
    fun refresh(): Observable<Unit>

    fun render(state: AppViewState)
}