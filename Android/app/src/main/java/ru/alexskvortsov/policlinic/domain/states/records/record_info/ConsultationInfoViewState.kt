package ru.alexskvortsov.policlinic.domain.states.records.record_info

import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity
import ru.alexskvortsov.policlinic.domain.states.records.list.Record

data class ConsultationInfoViewState(
    val record: Record,
    val changedRecord: Record = record,
    val possibleProcedures: List<ProcedureEntity> = emptyList(),
    val started: Boolean = false,
    val shouldCloseAndReload: Boolean = false
) {

    val canSave: Boolean
        get() = !changedRecord.proceduresUsed.isNullOrEmpty() &&
                !changedRecord.doctorNotes.isNullOrBlank()
}