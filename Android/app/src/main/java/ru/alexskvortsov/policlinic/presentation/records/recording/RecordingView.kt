package ru.alexskvortsov.policlinic.presentation.records.recording

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.domain.states.records.recording.RecordingViewState

interface RecordingView : MvpView {

    fun patientIntent(): Observable<String> //patientId
    fun competenceIntent(): Observable<String> //competenceId
    fun dateIntent(): Observable<LocalDateTime>
    fun doctorIntent(): Observable<String> //doctorId
    fun startTimeIntent(): Observable<LocalDateTime>
    fun reasonIntent(): Observable<String>
    fun createRecordIntent(): Observable<Unit>

    fun render(state: RecordingViewState)
}