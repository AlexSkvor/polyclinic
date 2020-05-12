package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.data.storage.database.dao.RegistryStaffDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.UserDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.RegistryStaffEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.repository.RegistryProfileRepository
import ru.alexskvortsov.policlinic.domain.states.registry.RegistryPerson
import javax.inject.Inject

class RegistryProfileService @Inject constructor(
    private val scheduler: Scheduler,
    private val userDao: UserDao,
    private val registryDao: RegistryStaffDao,
    private val prefs: AppPrefs
) : RegistryProfileRepository {

    override fun getPerson(): Observable<RegistryPerson> = Single.zip(getUser(), getRegistry(),
            BiFunction<UserEntity, RegistryStaffEntity, RegistryPerson> { user, registry ->
                RegistryPerson(
                    name = registry.name,
                    surname = registry.surname,
                    fathersName = registry.fathersName,
                    login = user.login,
                    userId = user.id,
                    registryId = registry.id
                )
            }).toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    private fun getUser() = userDao.getUserEntityById(prefs.currentUser.userId)

    private fun getRegistry() = registryDao.getById(prefs.currentUser.realId)

    override fun isLoginUnique(login: String): Observable<Boolean> = userDao.countSameLogins(login, prefs.currentUser.userId)
        .map { it > 0 }
        .map { !it }
        .toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun savePerson(person: RegistryPerson): Completable =
        Completable.defer { userDao.updateLogin(person.login, prefs.currentUser.userId) }
            .andThen(registryDao.updateFullName(person.name, person.surname, person.fathersName, person.registryId))
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
}