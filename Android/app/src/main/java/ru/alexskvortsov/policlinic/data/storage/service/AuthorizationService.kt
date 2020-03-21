package ru.alexskvortsov.policlinic.data.storage.service

import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.RegistryStaffDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.UserDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserSecondaryEntity
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.flattenFlatMap
import ru.alexskvortsov.policlinic.domain.flattenMap
import ru.alexskvortsov.policlinic.domain.repository.AuthorizationRepository
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo
import java.lang.IllegalArgumentException
import javax.inject.Inject

class AuthorizationService @Inject constructor(
    private val scheduler: Scheduler,
    private val prefs: AppPrefs,
    private val doctorDao: DoctorDao,
    private val patientDao: PatientDao,
    private val registryStaffDao: RegistryStaffDao,
    private val userDao: UserDao
) : AuthorizationRepository {

    override fun getListUserEntity(userType: UserAuthInfo.UserType): Single<List<UserAuthInfo>> =
        when (userType) {
            UserAuthInfo.UserType.DOCTOR -> doctorDao.getAllDoctorsList().map { it as List<UserSecondaryEntity> }
            UserAuthInfo.UserType.REGISTRY -> registryStaffDao.getAllRegistryList().map { it as List<UserSecondaryEntity> }
            UserAuthInfo.UserType.PATIENT -> patientDao.getAllPatientsList().map { it as List<UserSecondaryEntity> }
        }.flattenMap {
                UserAuthInfo(
                    id = it.userId,
                    fullName = it.fullName,
                    initialSurnameLetter = it.initialSurnameLetter,
                    password = "",
                    type = it.type,
                    realId = it.realId,
                    userUseToApp = prefs.saveSignInUserId.indexOfFirst { id -> id == it.userId }
                )
            }.flattenFlatMap { user ->
                userDao.getUserEntityById(user.id)
                    .map { user.copy(fullName = if (it.login.isNotEmpty()) it.login else user.fullName, password = it.password) }.toObservable()
            }.subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())

    override fun getUser(userId: String): Single<UserAuthInfo> = Single.just(userId)
        .flatMap {
            when {
                doctorDao.existsWithUserId(it) > 0 -> doctorDao.getByUserId(it)
                registryStaffDao.existsWithUserId(it) > 0 -> registryStaffDao.getByUserId(it)
                patientDao.existsWithUserId(it) > 0 -> patientDao.getByUserId(it)
                else -> throw IllegalArgumentException("User With this userId: $it does nt exist!")
            }
        }.map {
            UserAuthInfo(
                id = it.userId,
                fullName = it.fullName,
                initialSurnameLetter = it.initialSurnameLetter,
                password = "",
                type = it.type,
                realId = it.realId,
                userUseToApp = prefs.saveSignInUserId.indexOfFirst { id -> id == it.userId }
            )
        }.flatMap { user ->
            userDao.getUserEntityById(user.id)
                .map { user.copy(fullName = if (it.login.isNotEmpty()) it.login else user.fullName, password = it.password) }
        }.subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())

    override fun saveSingInUser(currentUserOwn: UserAuthInfo) {
        prefs.currentUser = currentUserOwn
    }

    override fun setNewPassword(userId: String, newPassword: String): Single<UserAuthInfo> =
        userDao.updatePassword(userId, newPassword)
            .andThen(getUser(userId))

}