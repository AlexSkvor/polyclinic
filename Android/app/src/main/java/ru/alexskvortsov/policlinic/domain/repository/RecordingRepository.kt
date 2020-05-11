package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

interface RecordingRepository {
    fun getPatient(id: String): Single<PatientEntity>
    fun getAllCompetences(): Single<List<CompetenceEntity>>
    fun getCompetence(competenceId: String): Single<CompetenceEntity>
    fun getDoctorsForCompetence(competenceId: String): Single<List<DoctorEntity>>
    fun getPossibleTimeList(date: LocalDateTime, doctorId: String): Single<List<LocalDateTime>>
    fun getDoctor(doctorId: String): Single<DoctorEntity>
    fun createRecord(patientEntity: PatientEntity, startTime: LocalDateTime, doctorEntity: DoctorEntity, reason: String): Single<String>
}