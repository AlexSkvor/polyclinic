package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity
import ru.alexskvortsov.policlinic.domain.states.records.list.Record

interface FactConsultationRepository {
    fun saveFactConsultation(record: Record): Completable
    fun getAllProcedures(): Observable<List<ProcedureEntity>>
}