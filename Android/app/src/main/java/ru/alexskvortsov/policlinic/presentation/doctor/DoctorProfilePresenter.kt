package ru.alexskvortsov.policlinic.presentation.doctor

import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Observable.merge
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.doNothing
import ru.alexskvortsov.policlinic.domain.states.doctor.DoctorProfileInteractor
import ru.alexskvortsov.policlinic.domain.states.doctor.DoctorProfilePartialState
import ru.alexskvortsov.policlinic.domain.states.doctor.DoctorProfileViewState
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationAction
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationActionRelay
import timber.log.Timber
import javax.inject.Inject

class DoctorProfilePresenter @Inject constructor(
    private val interactor: DoctorProfileInteractor,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager,
    private val navigation: NavigationActionRelay
) : BaseMviPresenter<DoctorProfileView, DoctorProfileViewState>() {
    override fun bindIntents() {
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(
            actions.scan(DoctorProfileViewState(), reducer).distinctUntilChanged(),
            DoctorProfileView::render
        )
    }

    private val reducer = BiFunction<DoctorProfileViewState, DoctorProfilePartialState, DoctorProfileViewState> { oldState, it ->
        when (it) {
            is DoctorProfilePartialState.Loading -> oldState
            is DoctorProfilePartialState.Error -> oldState
            is DoctorProfilePartialState.DoctorLoaded -> DoctorProfileViewState(
                doctor = it.doctor,
                possibleCompetences = oldState.possibleCompetences
            )
            is DoctorProfilePartialState.LoginVerified -> {
                if (!oldState.verificationStarted) oldState
                else oldState.copy(verificationStarted = false, loginUnique = it.unique)
            }
            is DoctorProfilePartialState.NewName -> oldState.copy(changedDoctor = oldState.changedDoctor.copy(name = it.name))
            is DoctorProfilePartialState.NewSurname -> oldState.copy(changedDoctor = oldState.changedDoctor.copy(surname = it.surname))
            is DoctorProfilePartialState.NewFathersName -> oldState.copy(changedDoctor = oldState.changedDoctor.copy(fathersName = it.fathersName))
            is DoctorProfilePartialState.NewLogin -> oldState.copy(
                changedDoctor = oldState.changedDoctor.copy(login = it.login),
                verificationStarted = true
            )
            DoctorProfilePartialState.DoctorSaved -> oldState
            is DoctorProfilePartialState.NewPhone -> oldState.copy(changedDoctor = oldState.changedDoctor.copy(phone = it.phone))
            is DoctorProfilePartialState.CompetencesChange -> oldState.copy(changedDoctor = oldState.changedDoctor.copy(competenceList = it.list))
            is DoctorProfilePartialState.CompetenceLoaded -> oldState.copy(possibleCompetences = it.list)
        }
    }

    private fun subscribeActions(actions: Observable<DoctorProfilePartialState>) {
        actions.subscribe {
            when (it) {
                is DoctorProfilePartialState.NewPhone -> doNothing()
                is DoctorProfilePartialState.DoctorLoaded -> doNothing()
                is DoctorProfilePartialState.LoginVerified -> doNothing()
                is DoctorProfilePartialState.CompetencesChange -> doNothing()
                is DoctorProfilePartialState.NewName -> doNothing()
                is DoctorProfilePartialState.NewSurname -> doNothing()
                is DoctorProfilePartialState.NewFathersName -> doNothing()
                is DoctorProfilePartialState.NewLogin -> doNothing()
                is DoctorProfilePartialState.CompetenceLoaded -> doNothing()
                is DoctorProfilePartialState.Loading -> systemMessage.showProgress(it.flag)
                is DoctorProfilePartialState.Error -> {
                    Timber.e(it.e)
                    systemMessage.send(resourceManager.getString(R.string.errorHappened))
                    navigation.pushAction(NavigationAction.Back)
                }
                DoctorProfilePartialState.DoctorSaved -> systemMessage.send(resourceManager.getString(R.string.profileUpdated))
            }
        }.bind()
    }

    private fun getActions(): Observable<DoctorProfilePartialState> {
        val initAction = intent(DoctorProfileView::initIntent)
            .switchMap { interactor.loadDoctor() }

        val nameChangedAction = intent(DoctorProfileView::nameChangedIntent)
            .map { DoctorProfilePartialState.NewName(it) }

        val surnameChangedAction = intent(DoctorProfileView::surnameChangedIntent)
            .map { DoctorProfilePartialState.NewSurname(it) }

        val fathersNameChangedAction = intent(DoctorProfileView::fathersNameChangedIntent)
            .map { DoctorProfilePartialState.NewFathersName(it) }

        val phoneChangedAction = intent(DoctorProfileView::phoneChangedIntent)
            .map { DoctorProfilePartialState.NewPhone(it) }

        val competenceChangedAction = intent(DoctorProfileView::competencesChangeIntent)
            .map { DoctorProfilePartialState.CompetencesChange(it) }

        val loginChangedActionPure = intent(DoctorProfileView::loginChangedIntent).share()

        val loginVerifyAction = loginChangedActionPure
            .switchMap { interactor.verifyLogin(it) }

        val loginChangedAction = loginChangedActionPure
            .map { DoctorProfilePartialState.NewLogin(it) }

        val saveAction = intent(DoctorProfileView::saveIntent)
            .switchMapWithLastState { interactor.saveDoctor(changedDoctor) }

        val cancelAction = intent(DoctorProfileView::cancelIntent)
            .switchMapWithLastState { just(DoctorProfilePartialState.DoctorLoaded(doctor)) }

        val list = listOf(
            initAction, nameChangedAction, surnameChangedAction, fathersNameChangedAction,
            loginVerifyAction, loginChangedAction, saveAction, cancelAction,
            phoneChangedAction, competenceChangedAction
        )

        return merge(list)
    }
}