package ru.alexskvortsov.policlinic.presentation.records.recording

import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.functions.BiFunction
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.doNothing
import ru.alexskvortsov.policlinic.domain.states.records.recording.RecordingInteractor
import ru.alexskvortsov.policlinic.domain.states.records.recording.RecordingPartialState
import ru.alexskvortsov.policlinic.domain.states.records.recording.RecordingViewState
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import timber.log.Timber
import javax.inject.Inject

class RecordingPresenter @Inject constructor(
    private val interactor: RecordingInteractor,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager
) : BaseMviPresenter<RecordingView, RecordingViewState>() {

    override fun bindIntents() {
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(
            actions.scan(RecordingViewState(), reducer).distinctUntilChanged(),
            RecordingView::render
        )
    }

    private val reducer = BiFunction<RecordingViewState, RecordingPartialState, RecordingViewState> { oldState, it ->
        when (it) {
            is RecordingPartialState.PatientLoaded -> RecordingViewState(
                patientEntity = it.patient
            )
            is RecordingPartialState.AllCompetencesLoaded -> oldState.copy(allCompetencesList = it.competences)
            is RecordingPartialState.CompetenceChosen -> RecordingViewState(
                patientEntity = oldState.patientEntity,
                allCompetencesList = oldState.allCompetencesList,
                competenceEntity = it.competence
            )
            is RecordingPartialState.DateChosen -> RecordingViewState(
                patientEntity = oldState.patientEntity,
                allCompetencesList = oldState.allCompetencesList,
                competenceEntity = oldState.competenceEntity,
                date = it.date
            )
            is RecordingPartialState.AllDoctorsLoaded -> oldState.copy(allDoctorsList = it.doctors)
            is RecordingPartialState.DoctorChosen -> RecordingViewState(
                patientEntity = oldState.patientEntity, allCompetencesList = oldState.allCompetencesList,
                competenceEntity = oldState.competenceEntity, date = oldState.date, allDoctorsList = oldState.allDoctorsList, doctorEntity = it.doctor
            )
            is RecordingPartialState.StartTimesLoaded -> oldState.copy(allStartTimes = it.list)
            is RecordingPartialState.StartTimeChosen -> oldState.copy(startTime = it.time)
            is RecordingPartialState.Reason -> oldState.copy(reason = it.text)
            is RecordingPartialState.RecordCreated -> oldState.copy(recordCreatedId = it.recordId)
            is RecordingPartialState.Error -> oldState
            is RecordingPartialState.Loading -> oldState
        }
    }

    private fun getActions(): Observable<RecordingPartialState> {

        val patientIntent = intent(RecordingView::patientIntent)
            .switchMap { interactor.getPatient(it) }

        val competenceChosenIntent = intent(RecordingView::competenceIntent)
            .switchMap { interactor.getCompetence(it) }

        val dateIntent = intent(RecordingView::dateIntent)
            .doOnNext {
                if (!it.isAfter(LocalDateTime.now()))
                    systemMessage.sendToast(resourceManager.getString(R.string.chooseTomorrowOrLater))
            }
            .filter { it.isAfter(LocalDateTime.now()) }
            .switchMapWithLastState { interactor.getDoctorsForCompetence(it, competenceEntity?.id.orEmpty()) }

        val doctorChosenIntent = intent(RecordingView::doctorIntent)
            .switchMapWithLastState {
                requireNotNull(date)
                interactor.getPossibleTimeList(date, it)
            }

        val startTimeChosenIntent = intent(RecordingView::startTimeIntent)
            .map { RecordingPartialState.StartTimeChosen(it) }

        val reasonIntent = intent(RecordingView::reasonIntent)
            .map { RecordingPartialState.Reason(it) }

        val createRecordIntent = intent(RecordingView::createRecordIntent)
            .switchMapWithLastState {
                requireNotNull(patientEntity)
                requireNotNull(startTime)
                requireNotNull(doctorEntity)
                interactor.createRecord(patientEntity, startTime, doctorEntity, reason)
            }

        val list = listOf(
            patientIntent, competenceChosenIntent, dateIntent, doctorChosenIntent, startTimeChosenIntent, reasonIntent, createRecordIntent
        )
        return merge(list)
    }

    private fun subscribeActions(actions: Observable<RecordingPartialState>) {
        actions.subscribe {
            when (it) {
                is RecordingPartialState.Loading -> systemMessage.showProgress(it.flag)
                is RecordingPartialState.Error -> {
                    Timber.e(it.t)
                    systemMessage.send(resourceManager.getString(R.string.errorHappened))
                }
                is RecordingPartialState.RecordCreated -> systemMessage.send(resourceManager.getString(R.string.recordCreated))
                is RecordingPartialState.AllCompetencesLoaded -> doNothing()
                is RecordingPartialState.PatientLoaded -> doNothing()
                is RecordingPartialState.CompetenceChosen -> doNothing()
                is RecordingPartialState.DateChosen -> doNothing()
                is RecordingPartialState.AllDoctorsLoaded -> doNothing()
                is RecordingPartialState.DoctorChosen -> doNothing()
                is RecordingPartialState.StartTimesLoaded -> doNothing()
                is RecordingPartialState.StartTimeChosen -> doNothing()
                is RecordingPartialState.Reason -> doNothing()
            }
        }.bind()
    }
}