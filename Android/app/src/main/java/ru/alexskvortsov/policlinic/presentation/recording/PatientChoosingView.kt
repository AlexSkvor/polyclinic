package ru.alexskvortsov.policlinic.presentation.recording

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.recording.patient_choosing.PatientChoosingViewState

interface PatientChoosingView : MvpView {

    fun filterChangesIntent(): Observable<PatientChoosingViewState.Filter>
    fun valueChangesIntent(): Observable<String>

    fun render(state: PatientChoosingViewState)
}