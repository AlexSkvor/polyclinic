package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.dao.FactConsultationDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.FactConsultationToProceduresConnectionDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.ProcedureDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.FactConsultationEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.FactConsultationToProceduresConnectionEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.repository.FactConsultationRepository
import ru.alexskvortsov.policlinic.domain.states.records.list.Record
import java.util.*
import javax.inject.Inject

class FactConsultationService @Inject constructor(
    private val scheduler: Scheduler,
    private val procedureDao: ProcedureDao,
    private val factConsultationDao: FactConsultationDao,
    private val proceduresConnectionDao: FactConsultationToProceduresConnectionDao
) : FactConsultationRepository {

    override fun saveFactConsultation(record: Record): Completable {
        val startTime = requireNotNull(record.startTimeFact)
        val newFactId = UUID.randomUUID().toString()
        val factConsultation = FactConsultationEntity(
            id = newFactId,
            consultationId = record.consultationId,
            startTimeFact = startTime,
            endTimeFact = LocalDateTime.now(),
            notes = record.doctorNotes.orEmpty()
        )
        return Completable.fromAction { factConsultationDao.insert(factConsultation) }
            .andThen(Completable.fromAction {
                record.proceduresUsed?.forEach {
                    proceduresConnectionDao.insert(
                        FactConsultationToProceduresConnectionEntity(
                            id = UUID.randomUUID().toString(),
                            procedureId = it.id,
                            factConsultationId = newFactId
                        )
                    )
                }
            }).subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())

    }

    override fun getAllProcedures(): Observable<List<ProcedureEntity>> = procedureDao.getAll()
        .toObservable()
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())
}