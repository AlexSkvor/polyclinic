package ru.alexskvortsov.policlinic.presentation.records.record_info

import io.reactivex.Observable
import io.reactivex.Observable.*
import io.reactivex.functions.BiFunction
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.doNothing
import ru.alexskvortsov.policlinic.domain.states.records.record_info.*
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import timber.log.Timber
import javax.inject.Inject

class ConsultationInfoPresenter @Inject constructor(
    private val interactor: ConsultationInfoInteractor,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager,
    private val recordHost: RecordHost,
    private val updateListNotifier: UpdateListNotifier
) : BaseMviPresenter<ConsultationInfoView, ConsultationInfoViewState>() {

    override fun bindIntents() {
        val record = requireNotNull(recordHost.record)
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(
            actions.scan(ConsultationInfoViewState(record), reducer).distinctUntilChanged(),
            ConsultationInfoView::render
        )
    }

    private val reducer = BiFunction<ConsultationInfoViewState, ConsultationInfoPartialState, ConsultationInfoViewState> { oldState, it ->
        when (it) {
            is ConsultationInfoPartialState.Loading -> oldState
            is ConsultationInfoPartialState.Error -> oldState
            ConsultationInfoPartialState.CloseAndReloadList -> oldState.copy(shouldCloseAndReload = true)
            ConsultationInfoPartialState.Start -> oldState.copy(
                changedRecord = oldState.record.copy(
                    startTimeFact = LocalDateTime.now()
                ),
                started = true
            )
            ConsultationInfoPartialState.Undo -> ConsultationInfoViewState(
                record = oldState.record,
                possibleProcedures = oldState.possibleProcedures
            )
            is ConsultationInfoPartialState.AddProcedure -> oldState.copy(
                changedRecord = oldState.changedRecord.copy(
                    proceduresUsed = oldState.changedRecord.proceduresUsed.orEmpty() + it.procedure
                )
            )
            is ConsultationInfoPartialState.RemoveProcedure -> oldState.copy(
                changedRecord = oldState.changedRecord.copy(
                    proceduresUsed = oldState.changedRecord.proceduresUsed.orEmpty() - it.procedure
                )
            )
            is ConsultationInfoPartialState.ChangeNote -> oldState.copy(
                changedRecord = oldState.changedRecord.copy(
                    doctorNotes = it.note
                )
            )
            is ConsultationInfoPartialState.PossibleProceduresLoaded -> oldState.copy(possibleProcedures = it.procedures)
        }
    }

    private fun subscribeActions(actions: Observable<ConsultationInfoPartialState>) {
        actions.subscribe {
            return@subscribe when (it) {
                is ConsultationInfoPartialState.Loading -> systemMessage.showProgress(it.flag)
                is ConsultationInfoPartialState.Error -> {
                    Timber.e(it.e)
                    systemMessage.send(resourceManager.getString(R.string.errorHappened))
                }
                ConsultationInfoPartialState.CloseAndReloadList -> updateListNotifier.reload()
                ConsultationInfoPartialState.Start -> doNothing()
                ConsultationInfoPartialState.Undo -> doNothing()
                is ConsultationInfoPartialState.AddProcedure -> doNothing()
                is ConsultationInfoPartialState.RemoveProcedure -> doNothing()
                is ConsultationInfoPartialState.ChangeNote -> doNothing()
                is ConsultationInfoPartialState.PossibleProceduresLoaded -> doNothing()
            }
        }.bind()
    }

    private fun getActions(): Observable<ConsultationInfoPartialState> {

        val initIntent = intent(ConsultationInfoView::initialIntent)
            .switchMap { interactor.getPossibleProcedures() }

        val startIntent = intent(ConsultationInfoView::startIntent)
            .map { ConsultationInfoPartialState.Start }

        val undoAllIntent = intent(ConsultationInfoView::undoAllIntent)
            .map { ConsultationInfoPartialState.Undo }

        val saveIntent = intent(ConsultationInfoView::saveIntent)
            .switchMapWithLastState { interactor.saveFactRecord(changedRecord) }

        val cancelIntent = intent(ConsultationInfoView::cancelIntent)
            .switchMapWithLastState { interactor.cancelIntent(record.consultationId) }

        val addProcedureIntent = intent(ConsultationInfoView::addProcedureIntent)
            .switchMapWithLastState {
                if (started) just(ConsultationInfoPartialState.AddProcedure(it))
                else empty()
            }

        val removeProcedureIntent = intent(ConsultationInfoView::deleteProcedureIntent)
            .switchMapWithLastState {
                if (started) just(ConsultationInfoPartialState.RemoveProcedure(it))
                else empty()
            }

        val noteChangedIntent = intent(ConsultationInfoView::noteChangedIntent)
            .switchMapWithLastState {
                if (started) just(ConsultationInfoPartialState.ChangeNote(it))
                else empty()
            }

        val list = listOf(
            initIntent, startIntent, undoAllIntent, saveIntent, cancelIntent, addProcedureIntent, removeProcedureIntent, noteChangedIntent
        )

        return merge(list)
    }
}