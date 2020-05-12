package ru.alexskvortsov.policlinic.domain.states.records.list

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.repository.RecordsRepository
import javax.inject.Inject

class RecordsInteractor @Inject constructor(
    private val repository: RecordsRepository
) {

    fun getRecordsList(type: RecordsViewState.ListType): Observable<RecordsPartialState> = repository.getRecordsList(type)
        .map { RecordsPartialState.ListLoaded(it).partial() }
        .startWith(RecordsPartialState.Loading(true))
        .onErrorReturn { RecordsPartialState.Error(it) }
        .endWith(RecordsPartialState.Loading(false))
}