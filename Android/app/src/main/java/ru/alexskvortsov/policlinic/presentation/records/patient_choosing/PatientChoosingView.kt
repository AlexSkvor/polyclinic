package ru.alexskvortsov.policlinic.presentation.records.patient_choosing

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.records.patient_choosing.PatientChoosingViewState

interface PatientChoosingView : MvpView {

    fun filterChangesIntent(): Observable<PatientChoosingViewState.Filter>
    fun valueChangesIntent(): Observable<String>

    fun render(state: PatientChoosingViewState)
}