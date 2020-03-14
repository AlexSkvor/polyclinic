package ru.alexskvortsov.policlinic.presentation.auth

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.states.auth.*
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationAction
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationActionRelay
import timber.log.Timber
import javax.inject.Inject

class AuthorizationPresenter @Inject constructor(
    private val interactor: AuthorizationInteractor,
    private val appPrefsProvider: AppPrefs,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager,
    private val actionRouter: NavigationActionRelay
) : BaseMviPresenter<AuthorizationView, AuthorizationViewState>() {

    override fun bindIntents() {
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(actions.scan(AuthorizationViewState(), reducer), AuthorizationView::render)
    }

    private val reducer = BiFunction { oldState: AuthorizationViewState, it: AuthorizationPartialState ->
        when (it) {
            is AuthorizationPartialState.LoadingUsers -> oldState.copy(userList = it.userList, emptyListVisible = it.userList.isNotEmpty(), updateListUsers = true, authInfo = null)
            is AuthorizationPartialState.UserSuccessPassword -> oldState.copy(updateListUsers = false, authInfo = null)
            is AuthorizationPartialState.SearchUserList -> oldState.copy(userList = it.userList, updateListUsers = true, authInfo = null)
            is AuthorizationPartialState.UserChangePassword -> oldState.copy(userList = it.userList, updateListUsers = true, authInfo = null)
            is AuthorizationPartialState.CancelChangePassword -> oldState.copy(updateListUsers = false, authInfo = null)
            is AuthorizationPartialState.UserFailedLogin -> oldState.copy(updateListUsers = false, authInfo = fillAuthInfo(FieldType.LOGIN_PASSWORD, PasswordState.INVALID))
            is AuthorizationPartialState.ErrorChangeWrongCurrentPassword -> oldState.copy(updateListUsers = false, authInfo = fillAuthInfo(FieldType.OLD_PASSWORD, PasswordState.INVALID))
            is AuthorizationPartialState.ErrorChangeNewPasswordNoNumbers -> oldState.copy(updateListUsers = false, authInfo = fillAuthInfo(FieldType.CHANGE_PASSWORD, PasswordState.INVALID))
            is AuthorizationPartialState.ErrorChangeNewPasswordNoLetters -> oldState.copy(updateListUsers = false, authInfo = fillAuthInfo(FieldType.CHANGE_PASSWORD, PasswordState.INVALID))
            is AuthorizationPartialState.ErrorChangeNewPasswordOnlyNumbersAndLettersAllowed -> oldState.copy(updateListUsers = false, authInfo = fillAuthInfo(FieldType.CHANGE_PASSWORD, PasswordState.INVALID))
            is AuthorizationPartialState.ChangeNewAndOldPasswordsNotEquals -> oldState.copy(updateListUsers = false, authInfo = fillAuthInfo(FieldType.REPEAT_CHANGE_PASSWORD, PasswordState.NOT_EQUAL))
            is AuthorizationPartialState.UserType -> oldState.copy(userListType = it.type)
        }
    }

    private fun fillAuthInfo(fieldType: FieldType, passState: PasswordState) = AuthElementInfo(fieldType, passState)

    private fun subscribeActions(actions: Observable<AuthorizationPartialState>) {
        actions.subscribe({
            when (it) {
                is AuthorizationPartialState.UserSuccessPassword -> {
                    saveLogInUserToRecent(it.userId)
                    appPrefsProvider.localAuthorisation = false
                    actionRouter.pushAction(NavigationAction.SignIn(it.type))
                }
                is AuthorizationPartialState.CancelChangePassword -> systemMessage.send(resourceManager.getString(R.string.password_change_canceled))
                is AuthorizationPartialState.UserChangePassword -> systemMessage.send(resourceManager.getString(R.string.done_change_password))
                is AuthorizationPartialState.ErrorChangeNewPasswordNoNumbers -> systemMessage.send(resourceManager.getString(R.string.need_one_numeral))
                is AuthorizationPartialState.ErrorChangeNewPasswordNoLetters -> systemMessage.send(resourceManager.getString(R.string.need_one_letter))
                is AuthorizationPartialState.ErrorChangeNewPasswordOnlyNumbersAndLettersAllowed -> systemMessage.send(resourceManager.getString(R.string.size_password_invalid))
            }
        }, { Timber.e(it) }).bind()
    }

    private fun saveLogInUserToRecent(userId: String) {
        val oldRecent = appPrefsProvider.saveSignInUserId
        if (oldRecent.contains(userId)) return reorderRecent(userId)
        appPrefsProvider.saveSignInUserId = if (oldRecent.isEmpty()) listOf(userId)
        else listOf(userId, oldRecent.first())
    }

    private fun reorderRecent(userId: String) {
        val oldRecent = appPrefsProvider.saveSignInUserId.take(2)
        if (oldRecent.size == 2 && oldRecent.first() != userId)
            appPrefsProvider.saveSignInUserId = oldRecent.reversed()
    }

    private fun getActions(): Observable<AuthorizationPartialState> {
        val intentFirstListUser = intent(AuthorizationView::getUserList)
            .switchMapWithLastState { interactor.getUsers(userListType) }

        val intentLoginUser = intent(AuthorizationView::tryLogin)
            .switchMap { interactor.checkInputUserPassword(it) }

        val intentSearchUser = intent(AuthorizationView::searchUser)
            .switchMapWithLastState { interactor.searchUser(it, userListType) }

        val userTypeChangedIntent = intent(AuthorizationView::userType)
            .map { AuthorizationPartialState.UserType(it) }

        val intentChangeUserPassword = intent(AuthorizationView::tryChangePasswordUser)
            .doOnNext { Timber.e(it.toString()) }
            .switchMapWithLastState { interactor.changePassword(it, userListType) }

        val intentCancelPassword = intent(AuthorizationView::cancelEditPassword)
            .map { AuthorizationPartialState.CancelChangePassword }

        val list = listOf(
            intentLoginUser,
            intentFirstListUser,
            intentSearchUser,
            intentChangeUserPassword,
            intentCancelPassword,
            userTypeChangedIntent
        )

        return Observable.merge(list)
    }
}