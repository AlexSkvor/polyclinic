package ru.alexskvortsov.policlinic.presentation.records.list

import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.functions.BiFunction
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.SystemMessage
import ru.alexskvortsov.policlinic.domain.doNothing
import ru.alexskvortsov.policlinic.domain.states.records.list.RecordsInteractor
import ru.alexskvortsov.policlinic.domain.states.records.list.RecordsPartialState
import ru.alexskvortsov.policlinic.domain.states.records.list.RecordsViewState
import ru.alexskvortsov.policlinic.presentation.base.BaseMviPresenter
import timber.log.Timber
import javax.inject.Inject

class RecordsPresenter @Inject constructor(
    private val interactor: RecordsInteractor,
    private val systemMessage: SystemMessage,
    private val resourceManager: ResourceManager
) : BaseMviPresenter<RecordsView, RecordsViewState>() {

    override fun bindIntents() {
        val actions = getActions().share()
        subscribeActions(actions)
        subscribeViewState(
            actions.scan(RecordsViewState(), reducer).distinctUntilChanged(),
            RecordsView::render
        )
    }

    private val reducer = BiFunction<RecordsViewState, RecordsPartialState, RecordsViewState> { oldState, it ->
        when (it) {
            is RecordsPartialState.Loading -> oldState
            is RecordsPartialState.Error -> oldState
            is RecordsPartialState.ListTypeChanged -> oldState.copy(listType = it.type)
            is RecordsPartialState.ListLoaded -> oldState.copy(list = it.list)
        }
    }

    private fun subscribeActions(actions: Observable<RecordsPartialState>) {
        actions.subscribe {
            when (it) {
                is RecordsPartialState.Loading -> systemMessage.showProgress(it.flag)
                is RecordsPartialState.Error -> {
                    Timber.e(it.e)
                    systemMessage.send(resourceManager.getString(R.string.errorHappened))
                }
                is RecordsPartialState.ListTypeChanged -> doNothing()
                is RecordsPartialState.ListLoaded -> doNothing()
            }
        }.bind()
    }

    private fun getActions(): Observable<RecordsPartialState> {
        val typeChangeIntent = intent(RecordsView::typeChangeIntent).share()

        val reloadIntent = intent(RecordsView::reloadIntent)
            .switchMapWithLastState { interactor.getRecordsList(listType) }

        val changeTypeIntent = typeChangeIntent
            .map { RecordsPartialState.ListTypeChanged(it) }

        val loadListIntent = typeChangeIntent
            .switchMap { interactor.getRecordsList(it) }

        val list = listOf(changeTypeIntent, loadListIntent, reloadIntent)
        return merge(list)
    }
}