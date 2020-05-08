package ru.alexskvortsov.policlinic.presentation.patient

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.patient.PatientProfileViewState

interface PatientProfileView : MvpView {

    fun initIntent(): Observable<Unit>

    fun nameChangedIntent(): Observable<String>
    fun surnameChangedIntent(): Observable<String>
    fun fathersNameChangedIntent(): Observable<String>
    fun loginChangedIntent(): Observable<String>

    fun passportChangedIntent(): Observable<String>
    fun omsChangedIntent(): Observable<String>

    fun weightChangedIntent(): Observable<String>
    fun heightChangedIntent(): Observable<String>

    fun saveIntent(): Observable<Unit>
    fun cancelIntent(): Observable<Unit>

    fun render(state: PatientProfileViewState)
}