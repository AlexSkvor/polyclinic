package ru.alexskvortsov.policlinic.domain.states.auth

import io.reactivex.Observable
import io.reactivex.Observable.just
import ru.alexskvortsov.policlinic.domain.flattenFilter
import ru.alexskvortsov.policlinic.domain.repository.AuthorizationRepository
import java.util.*
import javax.inject.Inject

class AuthorizationInteractor @Inject constructor(
    private val passwordValidation: PasswordValidation,
    private val repository: AuthorizationRepository
) {

    fun getUsers(type: UserAuthInfo.UserType): Observable<AuthorizationPartialState> =
        repository.getListUserEntity(type)
            .map<AuthorizationPartialState> { AuthorizationPartialState.LoadingUsers(it) }.toObservable()

    fun checkInputUserPassword(input: InputUserPassword): Observable<AuthorizationPartialState> =
        repository.getUser(input.userId)
            .flatMapObservable { user ->
                if (!passwordValidation.fullValidationPassword(input.password) || input.password != user.password) {
                    just(AuthorizationPartialState.UserFailedLogin)
                } else {
                    repository.saveSingInUser(user)
                    just(AuthorizationPartialState.UserSuccessPassword(user.id, user.type))
                }
            }

    fun searchUser(queryNameUser: String, type: UserAuthInfo.UserType): Observable<AuthorizationPartialState> =
        repository.getListUserEntity(type)
            .flattenFilter { it.fullName.toUpperCase(Locale.ROOT).contains(queryNameUser.toUpperCase(Locale.ROOT)) }
            .map { AuthorizationPartialState.SearchUserList(it).partial() }.toObservable()

    fun changePassword(input: ChangePasswordUser, userType: UserAuthInfo.UserType): Observable<AuthorizationPartialState> =
        repository.getUser(input.userId)
            .flatMapObservable {
                when {
                    !passwordValidation.fullValidationPassword(input.currentPassword) || input.newPassword != it.password -> just(AuthorizationPartialState.ErrorChangeWrongCurrentPassword)
                    passwordValidation.validationMinSizePassword(input.newPassword) && !passwordValidation.validationNumberMeet(input.newPassword) -> just(AuthorizationPartialState.ErrorChangeNewPasswordNoNumbers)
                    passwordValidation.validationMinSizePassword(input.newPassword) && !passwordValidation.validationLetterMeet(input.newPassword) -> just(AuthorizationPartialState.ErrorChangeNewPasswordNoLetters)
                    !passwordValidation.fullValidationPassword(input.newPassword) -> just(AuthorizationPartialState.ErrorChangeNewPasswordOnlyNumbersAndLettersAllowed)
                    input.newPassword != input.retryNewPassword -> just(AuthorizationPartialState.ChangeNewAndOldPasswordsNotEquals)
                    else -> saveUserAndGetListUsers(input, userType)
                        .map<AuthorizationPartialState> { users -> AuthorizationPartialState.UserChangePassword(users) }
                }
            }

    private fun saveUserAndGetListUsers(input: ChangePasswordUser, userType: UserAuthInfo.UserType): Observable<List<UserAuthInfo>> =
         repository.setNewPassword(input.userId, input.newPassword).toObservable()
            .flatMap { repository.getListUserEntity(userType).toObservable() }
}