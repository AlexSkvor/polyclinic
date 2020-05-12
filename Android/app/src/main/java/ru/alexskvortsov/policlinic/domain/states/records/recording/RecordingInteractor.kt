package ru.alexskvortsov.policlinic.domain.states.records.recording

import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.repository.RecordingRepository
import javax.inject.Inject

class RecordingInteractor @Inject constructor(
    private val repository: RecordingRepository
) {

    fun getPatient(id: String) = repository.getPatient(id).toObservable()
        .map { RecordingPartialState.PatientLoaded(it).partial() }
        .startWith(RecordingPartialState.Loading(true))
        .onErrorReturn { RecordingPartialState.Error(it) }
        .concatWith(loadAllCompetences())
        .endWith(RecordingPartialState.Loading(false))

    private fun loadAllCompetences() = repository.getAllCompetences().toObservable()
        .map { RecordingPartialState.AllCompetencesLoaded(it).partial() }
        .onErrorReturn { RecordingPartialState.Error(it) }

    fun getCompetence(id: String) = repository.getCompetence(id).toObservable()
        .map { RecordingPartialState.CompetenceChosen(it).partial() }
        .startWith(RecordingPartialState.Loading(true))
        .onErrorReturn { RecordingPartialState.Error(it) }
        .endWith(RecordingPartialState.Loading(false))

    fun getDoctorsForCompetence(dateTime: LocalDateTime, competenceId: String) =
        repository.getDoctorsForCompetence(competenceId).toObservable()
            .map { RecordingPartialState.AllDoctorsLoaded(it).partial() }
            .startWith(RecordingPartialState.DateChosen(dateTime))
            .startWith(RecordingPartialState.Loading(true))
            .onErrorReturn { RecordingPartialState.Error(it) }
            .endWith(RecordingPartialState.Loading(false))

    fun getPossibleTimeList(date: LocalDateTime, doctorId: String) =
        repository.getPossibleTimeList(date, doctorId).toObservable()
            .map { RecordingPartialState.StartTimesLoaded(it).partial() }
            .startWith(getDoctor(doctorId))
            .startWith(RecordingPartialState.Loading(true))
            .onErrorReturn { RecordingPartialState.Error(it) }
            .endWith(RecordingPartialState.Loading(false))

    private fun getDoctor(id: String) = repository.getDoctor(id).toObservable()
        .map { RecordingPartialState.DoctorChosen(it).partial() }
        .onErrorReturn { RecordingPartialState.Error(it) }

    fun createRecord(patientEntity: PatientEntity, startTime: LocalDateTime, doctorEntity: DoctorEntity, reason: String) =
        repository.createRecord(patientEntity, startTime, doctorEntity, reason).toObservable()
            .map { RecordingPartialState.RecordCreated(it).partial() }
            .startWith(RecordingPartialState.Loading(true))
            .onErrorReturn { RecordingPartialState.Error(it) }
            .endWith(RecordingPartialState.Loading(false))
}