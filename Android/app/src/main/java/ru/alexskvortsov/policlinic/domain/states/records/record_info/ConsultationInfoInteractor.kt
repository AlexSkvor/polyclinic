package ru.alexskvortsov.policlinic.domain.states.records.record_info

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.repository.CancelConsultationRepository
import ru.alexskvortsov.policlinic.domain.repository.FactConsultationRepository
import ru.alexskvortsov.policlinic.domain.states.records.list.Record
import ru.alexskvortsov.policlinic.domain.toObservableWithDefault
import javax.inject.Inject

class ConsultationInfoInteractor @Inject constructor(
    private val createRepository: FactConsultationRepository,
    private val cancelRepository: CancelConsultationRepository
) {

    fun getPossibleProcedures(): Observable<ConsultationInfoPartialState> = createRepository.getAllProcedures()
        .map { ConsultationInfoPartialState.PossibleProceduresLoaded(it).partial() }
        .startWith(ConsultationInfoPartialState.Loading(true))
        .onErrorReturn { ConsultationInfoPartialState.Error(it) }
        .endWith(ConsultationInfoPartialState.Loading(false))

    fun saveFactRecord(record: Record): Observable<ConsultationInfoPartialState> = createRepository.saveFactConsultation(record)
        .toObservableWithDefault(ConsultationInfoPartialState.CloseAndReloadList.partial())
        .startWith(ConsultationInfoPartialState.Loading(true))
        .onErrorReturn { ConsultationInfoPartialState.Error(it) }
        .endWith(ConsultationInfoPartialState.Loading(false))

    fun cancelIntent(recordId: String): Observable<ConsultationInfoPartialState> = cancelRepository.cancelConsultation(recordId)
        .toObservableWithDefault(ConsultationInfoPartialState.CloseAndReloadList.partial())
        .startWith(ConsultationInfoPartialState.Loading(true))
        .onErrorReturn { ConsultationInfoPartialState.Error(it) }
        .endWith(ConsultationInfoPartialState.Loading(false))

}