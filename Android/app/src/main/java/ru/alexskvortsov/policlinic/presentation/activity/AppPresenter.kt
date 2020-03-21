package ru.alexskvortsov.policlinic.presentation.activity

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.storage.unpacking.UnpackingManager
import ru.alexskvortsov.policlinic.domain.states.activity.AppPartialState
import ru.alexskvortsov.policlinic.domain.states.activity.AppViewState
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationAction
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationActionRelay
import ru.alexskvortsov.policlinic.presentation.navigation.Screens
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class AppPresenter @Inject constructor(
    private val systemMessage: SystemMessage,
    private val navigationRelay: NavigationActionRelay,
    private val unpackingManager: UnpackingManager,
    private val router: Router,
    private val prefs: AppPrefs
) : BaseMviPresenter<AppView, AppViewState>() {
    override fun bindIntents() {
        val initialState = AppViewState()

        val actions = getActions()
        subscribeActions(actions)

        subscribeViewState(
            actions.scan(initialState, reducer).distinctUntilChanged(),
            AppView::render
        )
    }

    private val reducer = BiFunction { oldState: AppViewState, partial: AppPartialState ->
        when (partial) {
            is AppPartialState.Progress -> oldState.copy(progress = partial.value, render = AppViewState.Render.PROGRESS)
            is AppPartialState.Toast -> oldState.copy(toast = partial.message, render = AppViewState.Render.TOAST)
            is AppPartialState.Snackbar -> oldState.copy(snackBar = partial.message, render = AppViewState.Render.SNACK_BAR)
            is AppPartialState.Restore -> oldState.copy(render = AppViewState.Render.RESTORE)
            else -> oldState.copy(render = AppViewState.Render.NONE)
        }
    }

    private fun subscribeActions(actions: Observable<AppPartialState>) {
        actions.subscribe {
            when (it) {
                is AppPartialState.LogOut -> logOut()
                is AppPartialState.SignIn -> when(it.type){
                    UserAuthInfo.UserType.DOCTOR -> router.newRootScreen(Screens.DoctorScreen)
                    UserAuthInfo.UserType.REGISTRY -> router.newRootScreen(Screens.RegistryScreen)
                    UserAuthInfo.UserType.PATIENT -> router.newRootScreen(Screens.PatientScreen)
                }
            }
        }.bind()
    }

    private fun logOut() {
        prefs.logOut()
        router.newRootScreen(Screens.AuthScreen)
    }

    private fun getActions(): Observable<AppPartialState> {

        unpackingManager.unpackIfNeeded()
            .doOnError { Timber.e(it) }
            .subscribe()
            .bind()

        val restore = intent(AppView::restore)
            .map { AppPartialState.Restore }

        val toast = systemMessage.notifier
            .filter { it.type is SystemMessage.Type.Toast }
            .map { AppPartialState.Toast(it.text) }

        val snackbar = systemMessage.notifier
            .filter { it.type is SystemMessage.Type.Snackbar }
            .map { AppPartialState.Snackbar(it.text) }

        val progress = systemMessage.notifier
            .filter { it.type is SystemMessage.Type.Progress }
            .map { AppPartialState.Progress(it.progress) }

        val signIn = navigationRelay.get()
            .ofType(NavigationAction.SignIn::class.java)
            .map { AppPartialState.SignIn(it.type) }

        val logOut = navigationRelay.get()
            .filter { it is NavigationAction.LogOut }
            .map { AppPartialState.LogOut }

        val refresh = intent(AppView::refresh)
            .map { AppPartialState.Refresh }

        val actions = listOf(restore, refresh, toast, snackbar, progress, signIn, logOut)
        return Observable.merge(actions).share()
    }
}