package ru.alexskvortsov.policlinic.presentation.doctor

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.domain.states.doctor.DoctorProfileViewState

interface DoctorProfileView : MvpView {

    fun initIntent(): Observable<Unit>

    fun nameChangedIntent(): Observable<String>
    fun surnameChangedIntent(): Observable<String>
    fun fathersNameChangedIntent(): Observable<String>
    fun loginChangedIntent(): Observable<String>
    fun phoneChangedIntent(): Observable<String>

    fun competencesChangeIntent(): Observable<List<CompetenceEntity>>

    fun saveIntent(): Observable<Unit>
    fun cancelIntent(): Observable<Unit>

    fun render(state: DoctorProfileViewState)
}