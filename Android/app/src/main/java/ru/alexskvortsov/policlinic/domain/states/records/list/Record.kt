package ru.alexskvortsov.policlinic.domain.states.records.list

import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.entities.*

data class Record(
    val consultationId: String,
    val doctorEntity: DoctorEntity,
    val patientEntity: PatientEntity,
    val competenceEntity: CompetenceEntity,
    val createdUserEntity: UserEntity,
    val startTimePlan: LocalDateTime,
    val endTimePlan: LocalDateTime,
    val reason: String,
    val cancelled: Boolean,
    val proceduresUsed: List<ProcedureEntity>?,
    val startTimeFact: LocalDateTime?,
    val endTimeFact: LocalDateTime?,
    val doctorNotes: String?
) {
    val passed
        get() = endTimeFact != null || cancelled
}