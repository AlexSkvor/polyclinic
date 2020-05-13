package ru.alexskvortsov.policlinic.presentation.records.record_info

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity
import ru.alexskvortsov.policlinic.domain.states.records.record_info.ConsultationInfoViewState

interface ConsultationInfoView : MvpView {

    fun initialIntent(): Observable<Unit>

    fun startIntent(): Observable<Unit>
    fun undoAllIntent(): Observable<Unit>
    fun saveIntent(): Observable<Unit>

    fun cancelIntent(): Observable<Unit>

    fun addProcedureIntent(): Observable<ProcedureEntity>
    fun deleteProcedureIntent(): Observable<ProcedureEntity>
    fun noteChangedIntent(): Observable<String>

    fun render(state: ConsultationInfoViewState)

}