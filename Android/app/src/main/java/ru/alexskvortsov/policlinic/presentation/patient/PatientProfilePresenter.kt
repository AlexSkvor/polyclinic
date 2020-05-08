package ru.alexskvortsov.policlinic.presentation.patient

import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Observable.merge
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.doNothing
import ru.alexskvortsov.policlinic.domain.states.patient.PatientProfileInteractor
import ru.alexskvortsov.policlinic.domain.states.patient.PatientProfilePartialState
import ru.alexskvortsov.policlinic.domain.states.patient.PatientProfileViewState
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationAction
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationActionRelay
import timber.log.Timber
import javax.inject.Inject

class PatientProfilePresenter @Inject constructor(
    private val interactor: PatientProfileInteractor,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager,
    private val navigation: NavigationActionRelay
) : BaseMviPresenter<PatientProfileView, PatientProfileViewState>() {
    override fun bindIntents() {
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(
            actions.scan(PatientProfileViewState(), reducer).distinctUntilChanged(),
            PatientProfileView::render
        )
    }

    private val reducer = BiFunction<PatientProfileViewState, PatientProfilePartialState, PatientProfileViewState> { oldState, it ->
        when (it) {
            PatientProfilePartialState.PatientSaved -> oldState
            is PatientProfilePartialState.Loading -> oldState
            is PatientProfilePartialState.Error -> oldState
            is PatientProfilePartialState.PersonLoaded -> PatientProfileViewState(it.person)
            is PatientProfilePartialState.LoginVerified -> {
                if (!oldState.verificationStarted) oldState
                else oldState.copy(verificationStarted = false, loginUnique = it.unique)
            }
            is PatientProfilePartialState.NewName -> oldState.copy(changedPatient = oldState.changedPatient.copy(name = it.name))
            is PatientProfilePartialState.NewSurname -> oldState.copy(changedPatient = oldState.changedPatient.copy(surname = it.surname))
            is PatientProfilePartialState.NewFathersName -> oldState.copy(changedPatient = oldState.changedPatient.copy(fathersName = it.fathersName))
            is PatientProfilePartialState.NewLogin -> oldState.copy(
                changedPatient = oldState.changedPatient.copy(login = it.login),
                verificationStarted = true
            )
            is PatientProfilePartialState.NewPassportNumber -> oldState.copy(changedPatient = oldState.changedPatient.copy(passportNumber = it.passportNumber))
            is PatientProfilePartialState.NewOmsNumber -> oldState.copy(changedPatient = oldState.changedPatient.copy(omsPoliceNumber = it.omsNumber))
            is PatientProfilePartialState.NewHeight -> oldState.copy(changedPatient = oldState.changedPatient.copy(height = it.height))
            is PatientProfilePartialState.NewWeight -> oldState.copy(changedPatient = oldState.changedPatient.copy(weight = it.weight))
        }
    }

    private fun subscribeActions(actions: Observable<PatientProfilePartialState>) {
        actions.subscribe {
            when (it) {
                is PatientProfilePartialState.Loading -> systemMessage.showProgress(it.flag)
                is PatientProfilePartialState.Error -> {
                    Timber.e(it.e)
                    systemMessage.send(resourceManager.getString(R.string.errorHappened))
                    navigation.pushAction(NavigationAction.Back)
                }
                PatientProfilePartialState.PatientSaved -> systemMessage.send(resourceManager.getString(R.string.profileUpdated))
                is PatientProfilePartialState.PersonLoaded -> doNothing()
                is PatientProfilePartialState.LoginVerified -> doNothing()
                is PatientProfilePartialState.NewName -> doNothing()
                is PatientProfilePartialState.NewSurname -> doNothing()
                is PatientProfilePartialState.NewFathersName -> doNothing()
                is PatientProfilePartialState.NewLogin -> doNothing()
                is PatientProfilePartialState.NewPassportNumber -> doNothing()
                is PatientProfilePartialState.NewOmsNumber -> doNothing()
                is PatientProfilePartialState.NewHeight -> doNothing()
                is PatientProfilePartialState.NewWeight -> doNothing()
            }
        }.bind()
    }

    private fun getActions(): Observable<PatientProfilePartialState> {
        val initAction = intent(PatientProfileView::initIntent)
            .switchMap { interactor.loadPatient() }

        val nameChangedAction = intent(PatientProfileView::nameChangedIntent)
            .map { PatientProfilePartialState.NewName(it) }

        val surnameChangedAction = intent(PatientProfileView::surnameChangedIntent)
            .map { PatientProfilePartialState.NewSurname(it) }

        val fathersNameChangedAction = intent(PatientProfileView::fathersNameChangedIntent)
            .map { PatientProfilePartialState.NewFathersName(it) }

        val loginChangedActionPure = intent(PatientProfileView::loginChangedIntent).share()

        val loginVerifyAction = loginChangedActionPure
            .switchMap { interactor.verifyLogin(it) }

        val loginChangedAction = loginChangedActionPure
            .map { PatientProfilePartialState.NewLogin(it) }

        val passportChangedAction = intent(PatientProfileView::passportChangedIntent)
            .map { PatientProfilePartialState.NewPassportNumber(it) }

        val omsChangedAction = intent(PatientProfileView::omsChangedIntent)
            .map { PatientProfilePartialState.NewOmsNumber(it) }

        val weightChangedAction = intent(PatientProfileView::weightChangedIntent)
            .map { PatientProfilePartialState.NewWeight(it) }

        val heightChangedAction = intent(PatientProfileView::heightChangedIntent)
            .map { PatientProfilePartialState.NewHeight(it) }

        val saveAction = intent(PatientProfileView::saveIntent)
            .switchMapWithLastState { interactor.savePatient(changedPatient) }

        val cancelAction = intent(PatientProfileView::cancelIntent)
            .switchMapWithLastState { just(PatientProfilePartialState.PersonLoaded(patient)) }

        val list = listOf(
            initAction, nameChangedAction, surnameChangedAction, fathersNameChangedAction,
            loginVerifyAction, loginChangedAction, saveAction, cancelAction,
            passportChangedAction, omsChangedAction, weightChangedAction, heightChangedAction
        )

        return merge(list)
    }
}