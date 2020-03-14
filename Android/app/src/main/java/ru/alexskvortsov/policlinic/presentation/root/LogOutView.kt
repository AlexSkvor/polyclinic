package ru.alexskvortsov.policlinic.presentation.root

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface LogOutView: MvpView {
    fun initLogout(): Observable<Unit>
    fun logOutIntent(): Observable<Unit>
}