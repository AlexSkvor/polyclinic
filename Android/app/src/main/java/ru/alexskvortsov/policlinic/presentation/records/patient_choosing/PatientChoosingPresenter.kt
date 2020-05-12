package ru.alexskvortsov.policlinic.presentation.records.patient_choosing

import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.doNothing
import ru.alexskvortsov.policlinic.domain.states.records.patient_choosing.PatientChoosingInteractor
import ru.alexskvortsov.policlinic.domain.states.records.patient_choosing.PatientChoosingPartialState
import ru.alexskvortsov.policlinic.domain.states.records.patient_choosing.PatientChoosingViewState
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import timber.log.Timber
import javax.inject.Inject

class PatientChoosingPresenter @Inject constructor(
    private val interactor: PatientChoosingInteractor,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager
) : BaseMviPresenter<PatientChoosingView, PatientChoosingViewState>() {
    override fun bindIntents() {
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(
            actions.scan(PatientChoosingViewState(), reducer).distinctUntilChanged(),
            PatientChoosingView::render
        )
    }

    private val reducer = BiFunction<PatientChoosingViewState, PatientChoosingPartialState, PatientChoosingViewState> { oldState, it ->
        when (it) {
            is PatientChoosingPartialState.Loading -> oldState
            is PatientChoosingPartialState.Error -> oldState
            is PatientChoosingPartialState.PatientsLoaded -> oldState.copy(patientsList = it.list)
            is PatientChoosingPartialState.ValueChanged -> oldState.copy(interredValue = it.text, patientsList = emptyList())
            is PatientChoosingPartialState.FilterChanged -> PatientChoosingViewState(chosenFilter = it.newFilter)
        }
    }

    private fun subscribeActions(actions: Observable<PatientChoosingPartialState>) {
        actions.subscribe {
            when (it) {
                is PatientChoosingPartialState.Loading -> systemMessage.showProgress(it.flag)
                is PatientChoosingPartialState.Error -> {
                    Timber.e(it.e)
                    systemMessage.send(resourceManager.getString(R.string.errorHappened))
                }
                is PatientChoosingPartialState.PatientsLoaded -> doNothing()
                is PatientChoosingPartialState.ValueChanged -> doNothing()
                is PatientChoosingPartialState.FilterChanged -> doNothing()
            }
        }.bind()
    }

    private fun getActions(): Observable<PatientChoosingPartialState> {
        val filterChangedIntent = intent(PatientChoosingView::filterChangesIntent)
            .map { PatientChoosingPartialState.FilterChanged(it) }

        val valueChangedIntent = intent(PatientChoosingView::valueChangesIntent)
            .switchMapWithLastState { interactor.valueChanged(chosenFilter, it) }

        val list = listOf(filterChangedIntent, valueChangedIntent)

        return merge(list)
    }
}