package ru.alexskvortsov.policlinic.presentation.auth

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.auth.AuthorizationViewState
import ru.alexskvortsov.policlinic.domain.states.auth.ChangePasswordUser
import ru.alexskvortsov.policlinic.domain.states.auth.InputUserPassword
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo

interface AuthorizationView: MvpView {

    fun getUserList(): Observable<Unit>
    fun searchUser(): Observable<String>
    fun tryLogin(): Observable<InputUserPassword>
    fun tryChangePasswordUser(): Observable<ChangePasswordUser>
    fun cancelEditPassword(): Observable<Unit>
    fun userType(): Observable<UserAuthInfo.UserType>

    fun render(viewState: AuthorizationViewState)
}