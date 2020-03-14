package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Single
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo

interface AuthorizationRepository {
    fun getListUserEntity(userType: UserAuthInfo.UserType): Single<List<UserAuthInfo>>
    fun getUser(userId: String): Single<UserAuthInfo>
    fun saveSingInUser(currentUserOwn: UserAuthInfo)
    fun setNewPassword(userId: String, newPassword: String): Single<UserAuthInfo>
}