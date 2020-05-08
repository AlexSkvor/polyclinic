package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.threeten.bp.format.DateTimeFormatter
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.UserDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.repository.PatientProfileRepository
import ru.alexskvortsov.policlinic.domain.states.patient.PatientPerson
import javax.inject.Inject

class PatientProfileService @Inject constructor(
    private val scheduler: Scheduler,
    private val userDao: UserDao,
    private val patientDao: PatientDao,
    private val prefs: AppPrefs
) : PatientProfileRepository {

    override fun isLoginUnique(login: String): Observable<Boolean> = userDao.countSameLogins(login, prefs.currentUser.userId)
        .map { it > 0 }
        .map { !it }
        .toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getPatient(): Observable<PatientPerson> = Single.zip(userDao.getUserEntityById(prefs.currentUser.userId),
        patientDao.getByUserId(prefs.currentUser.userId),
        BiFunction<UserEntity, PatientEntity, PatientPerson> { user, patient ->
            PatientPerson(
                name = patient.name,
                surname = patient.surname,
                fathersName = patient.fathersName,
                login = user.login,
                userId = user.id,
                gender = patient.gender,
                berthDate = patient.berthDate.format(formatter),
                snilsNumber = patient.snilsNumber,
                height = patient.height.toString(),
                weight = patient.weight.toString(),
                omsPoliceNumber = patient.omsPoliceNumber,
                passportNumber = patient.passportNumber,
                patientId = patient.id
            )
        })
        .toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun savePatient(person: PatientPerson): Completable =
        Completable.defer { userDao.updateLogin(person.login, prefs.currentUser.userId) }
            .andThen(
                patientDao.updatePatient(
                    person.patientId, person.surname, person.name, person.fathersName,
                    person.passportNumber, person.omsPoliceNumber, person.weight.toInt(), person.height.toInt()
                )
            ).subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())

    private val formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy")
}