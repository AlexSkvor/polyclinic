package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.records.list.Record
import ru.alexskvortsov.policlinic.domain.states.records.list.RecordsViewState

interface RecordsRepository {
    fun getRecordsList(type: RecordsViewState.ListType): Observable<List<Record>>
}