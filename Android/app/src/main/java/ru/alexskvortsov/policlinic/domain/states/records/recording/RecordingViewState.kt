package ru.alexskvortsov.policlinic.domain.states.records.recording

import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

data class RecordingViewState(
    val patientEntity: PatientEntity? = null,
    val competenceEntity: CompetenceEntity? = null,
    val allCompetencesList: List<CompetenceEntity> = emptyList(),
    val date: LocalDateTime? = null,
    val doctorEntity: DoctorEntity? = null,
    val allDoctorsList: List<DoctorEntity> = emptyList(),
    val startTime: LocalDateTime? = null,
    val allStartTimes: List<LocalDateTime> = emptyList(),
    val reason: String = "",
    val recordCreatedId: String = ""
) {


}