package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.data.storage.database.dao.CompetenceDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorToCompetenceConnectionDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.UserDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorToCompetenceConnectionEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.repository.DoctorProfileRepository
import ru.alexskvortsov.policlinic.domain.states.doctor.DoctorPerson
import ru.alexskvortsov.policlinic.formatterDBDate
import java.util.*
import javax.inject.Inject

class DoctorProfileService @Inject constructor(
    private val scheduler: Scheduler,
    private val userDao: UserDao,
    private val doctorDao: DoctorDao,
    private val prefs: AppPrefs,
    private val competenceDao: CompetenceDao,
    private val doctorToCompetenceConnectionDao: DoctorToCompetenceConnectionDao
) : DoctorProfileRepository {
    override fun isLoginUnique(login: String): Observable<Boolean> = userDao.countSameLogins(login, prefs.currentUser.userId)
        .map { it > 0 }
        .map { !it }
        .toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getAllCompetences(): Observable<List<CompetenceEntity>> = competenceDao.getAll()
        .toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun getDoctor(): Observable<DoctorPerson> =
        Single.zip(userDao.getById(prefs.currentUser.userId),
            doctorDao.getByUserId(prefs.currentUser.userId),
            BiFunction<UserEntity, DoctorEntity, DoctorPerson> { user, doctor ->
                DoctorPerson(
                    name = doctor.name,
                    surname = doctor.surname,
                    fathersName = doctor.fathersName,
                    login = user.login,
                    userId = user.id,
                    doctorId = doctor.id,
                    skillLevel = doctor.skillLevel,
                    workExperienceYears = doctor.workExperienceYears,
                    educationMainDocumentRef = doctor.educationMainDocumentRef,
                    gender = doctor.gender,
                    berthDate = doctor.berthDate.format(formatterDBDate),
                    phone = doctor.phone,
                    competenceList = emptyList()
                )
            })
            .flatMap { doctor ->
                competenceDao.getByDoctorId(doctor.doctorId)
                    .map { doctor.copy(competenceList = it) }
            }
            .toObservable()
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())

    override fun saveDoctor(person: DoctorPerson): Completable =
        Completable.defer { userDao.updateLogin(person.login, prefs.currentUser.userId) }
            .andThen(doctorDao.updateFullNameAndPhone(person.name, person.surname, person.fathersName, person.phone, person.doctorId))
            .andThen(competenceDao.clearForDoctor(person.doctorId))
            .andThen(
                Completable.fromAction {
                    val connections = person.competenceList.map {
                        DoctorToCompetenceConnectionEntity(
                            id = UUID.randomUUID().toString(),
                            doctorId = person.doctorId,
                            competenceId = it.id
                        )

                    }
                    doctorToCompetenceConnectionDao.insertAll(connections)
                }
            )
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
}