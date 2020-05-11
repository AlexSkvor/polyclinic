package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.repository.PatientChoosingRepository
import javax.inject.Inject

class PatientChoosingService @Inject constructor(
    private val scheduler: Scheduler,
    private val patientDao: PatientDao
) : PatientChoosingRepository {

    override fun getPatientsByPassport(value: String): Observable<List<PatientEntity>> = patientDao.getByPassport(value).toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getPatientsByPhone(value: String): Observable<List<PatientEntity>> = patientDao.getByPhone(value).toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getPatientsByOms(value: String): Observable<List<PatientEntity>> = patientDao.getByOms(value).toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getPatientsBySnils(value: String): Observable<List<PatientEntity>> = patientDao.getBySnils(value).toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getPatientsBySurname(value: String): Observable<List<PatientEntity>> = patientDao.getBySurname(value).toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())
}