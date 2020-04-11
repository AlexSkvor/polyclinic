package ru.alexskvortsov.policlinic.presentation.registry

import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Observable.merge
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.doNothing
import ru.alexskvortsov.policlinic.domain.states.registry.profile.RegistryProfileInteractor
import ru.alexskvortsov.policlinic.domain.states.registry.profile.RegistryProfilePartialState
import ru.alexskvortsov.policlinic.domain.states.registry.profile.RegistryProfileViewState
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationAction
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationActionRelay
import timber.log.Timber
import javax.inject.Inject

class RegistryProfilePresenter @Inject constructor(
    private val interactor: RegistryProfileInteractor,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager,
    private val navigation: NavigationActionRelay
) : BaseMviPresenter<RegistryProfileView, RegistryProfileViewState>() {
    override fun bindIntents() {
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(
            actions.scan(RegistryProfileViewState(), reducer).distinctUntilChanged(),
            RegistryProfileView::render
        )
    }

    private fun subscribeActions(actions: Observable<RegistryProfilePartialState>) {
        actions.subscribe {
            when (it) {
                is RegistryProfilePartialState.Loading -> systemMessage.showProgress(it.flag)
                is RegistryProfilePartialState.PersonLoaded -> doNothing()
                is RegistryProfilePartialState.LoginVerified -> doNothing()
                is RegistryProfilePartialState.NewName -> doNothing()
                is RegistryProfilePartialState.NewSurname -> doNothing()
                is RegistryProfilePartialState.NewFathersName -> doNothing()
                is RegistryProfilePartialState.NewLogin -> doNothing()
                is RegistryProfilePartialState.Error -> {
                    Timber.e(it.e)
                    systemMessage.send(resourceManager.getString(R.string.errorHappened))
                    navigation.pushAction(NavigationAction.Back)
                }
                RegistryProfilePartialState.PersonSaved -> systemMessage.send(resourceManager.getString(R.string.profileUpdated))
            }
        }.bind()
    }

    private val reducer = BiFunction<RegistryProfileViewState, RegistryProfilePartialState, RegistryProfileViewState> { oldState, it ->
        when (it) {
            is RegistryProfilePartialState.Loading -> oldState
            is RegistryProfilePartialState.Error -> oldState
            is RegistryProfilePartialState.PersonLoaded -> RegistryProfileViewState(it.person)
            is RegistryProfilePartialState.LoginVerified -> {
                if (!oldState.verificationStarted) oldState
                else oldState.copy(verificationStarted = false, loginUnique = it.unique)
            }
            is RegistryProfilePartialState.NewName -> oldState.copy(changedPerson = oldState.changedPerson.copy(name = it.name))
            is RegistryProfilePartialState.NewSurname -> oldState.copy(changedPerson = oldState.changedPerson.copy(surname = it.surname))
            is RegistryProfilePartialState.NewFathersName -> oldState.copy(changedPerson = oldState.changedPerson.copy(fathersName = it.fathersName))
            is RegistryProfilePartialState.NewLogin -> oldState.copy(changedPerson = oldState.changedPerson.copy(login = it.login), verificationStarted = true)
            RegistryProfilePartialState.PersonSaved -> oldState
        }
    }

    private fun getActions(): Observable<RegistryProfilePartialState> {
        val initAction = intent(RegistryProfileView::initIntent)
            .switchMap { interactor.loadPerson() }

        val nameChangedAction = intent(RegistryProfileView::nameChangedIntent)
            .map { RegistryProfilePartialState.NewName(it) }

        val surnameChangedAction = intent(RegistryProfileView::surnameChangedIntent)
            .map { RegistryProfilePartialState.NewSurname(it) }

        val fathersNameChangedAction = intent(RegistryProfileView::fathersNameChangedIntent)
            .map { RegistryProfilePartialState.NewFathersName(it) }

        val loginChangedActionPure = intent(RegistryProfileView::loginChangedIntent).share()

        val loginVerifyAction = loginChangedActionPure
            .switchMap { interactor.verifyLogin(it) }

        val loginChangedAction = loginChangedActionPure
            .map { RegistryProfilePartialState.NewLogin(it) }

        val saveAction = intent(RegistryProfileView::saveIntent)
            .switchMapWithLastState { interactor.savePerson(changedPerson) }

        val cancelAction = intent(RegistryProfileView::cancelIntent)
            .switchMapWithLastState { just(RegistryProfilePartialState.PersonLoaded(person)) }

        val list = listOf(
            initAction, nameChangedAction, surnameChangedAction, fathersNameChangedAction,
            loginVerifyAction, loginChangedAction, saveAction, cancelAction
        )

        return merge(list)
    }
}