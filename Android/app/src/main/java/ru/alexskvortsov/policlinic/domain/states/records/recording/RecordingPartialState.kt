package ru.alexskvortsov.policlinic.domain.states.records.recording

import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

sealed class RecordingPartialState(private val log: String) {

    data class PatientLoaded(val patient: PatientEntity) : RecordingPartialState("PatientLoaded $patient")
    data class AllCompetencesLoaded(val competences: List<CompetenceEntity>) : RecordingPartialState("AllCompetencesLoaded $competences")
    data class CompetenceChosen(val competence: CompetenceEntity) : RecordingPartialState("CompetenceChosen $competence")
    data class DateChosen(val date: LocalDateTime) : RecordingPartialState("DateChosen $date")
    data class AllDoctorsLoaded(val doctors: List<DoctorEntity>) : RecordingPartialState("AllDoctorsLoaded $doctors")
    data class DoctorChosen(val doctor: DoctorEntity) : RecordingPartialState("DoctorChosen $doctor")
    data class StartTimesLoaded(val list: List<LocalDateTime>) : RecordingPartialState("StartTimesLoaded $list")
    data class StartTimeChosen(val time: LocalDateTime) : RecordingPartialState("StartTimeChosen $time")
    data class Reason(val text: String) : RecordingPartialState("Reason $text")

    data class RecordCreated(val recordId: String) : RecordingPartialState("RecordCreated $recordId")

    data class Error(val t: Throwable) : RecordingPartialState("Error $t")
    data class Loading(val flag: Boolean) : RecordingPartialState("Loading $flag")


    override fun toString(): String = log
    fun partial() = this
}