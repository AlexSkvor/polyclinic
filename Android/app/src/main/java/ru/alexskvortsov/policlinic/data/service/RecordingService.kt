package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import ru.alexskvortsov.policlinic.data.storage.database.dao.CompetenceDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.ConsultationDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.ConsultationEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.flattenMap
import ru.alexskvortsov.policlinic.domain.repository.RecordingRepository
import java.util.*
import javax.inject.Inject

class RecordingService @Inject constructor(
    private val scheduler: Scheduler,
    private val patientDao: PatientDao,
    private val doctorDao: DoctorDao,
    private val competenceDao: CompetenceDao,
    private val consultationDao: ConsultationDao,
    private val prefs: AppPrefs
) : RecordingRepository {
    override fun getPatient(id: String): Single<PatientEntity> = patientDao.getById(id)
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getAllCompetences(): Single<List<CompetenceEntity>> = competenceDao.getAll()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getCompetence(competenceId: String): Single<CompetenceEntity> = competenceDao.getById(competenceId)
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getDoctorsForCompetence(competenceId: String): Single<List<DoctorEntity>> = doctorDao.getByCompetenceId(competenceId)
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getDoctor(doctorId: String): Single<DoctorEntity> = doctorDao.getById(doctorId)
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getPossibleTimeList(date: LocalDateTime, doctorId: String): Single<List<LocalDateTime>> =
        consultationDao.getByDateAndDoctorId(date.toLocalDate(), doctorId)
            .flattenMap { it.startTimePlan }
            .map { getPossibleTimeListFromAlreadyPlannedList(it, date) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())

    private fun getPossibleTimeListFromAlreadyPlannedList(alreadyPlannedList: List<LocalDateTime>, date: LocalDateTime): List<LocalDateTime> =
        getAllPossibleStartTimesForDay(date)
            .filter { it !in alreadyPlannedList.filter { old -> old.minute == 30 || old.minute == 0 && old.second == 0 && old.hour in 8..17 } }

    private fun getAllPossibleStartTimesForDay(date: LocalDateTime): List<LocalDateTime> {
        val result = mutableListOf<LocalDateTime>()
        for (i in 0..19) {
            result.add(LocalDateTime.of(date.toLocalDate(), LocalTime.of(8 + i / 2, 30 * (i % 2), 0)))
        }
        return result
    }

    override fun createRecord(
        patientEntity: PatientEntity,
        startTime: LocalDateTime,
        doctorEntity: DoctorEntity,
        reason: String
    ): Single<String> {
        val newId = UUID.randomUUID().toString()
        val newConsultation = ConsultationEntity(
            id = newId,
            doctorId = doctorEntity.id,
            userAskedId = prefs.currentUser.userId,
            patientId = patientEntity.id,
            date = startTime.toLocalDate().atStartOfDay(),
            startTimePlan = startTime,
            endTimePlan = startTime.plusMinutes(30),
            descriptionOfReason = reason,
            cancelled = false
        )
        return Completable.fromAction { consultationDao.insert(newConsultation) }
            .toSingleDefault(newId)
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
    }
}