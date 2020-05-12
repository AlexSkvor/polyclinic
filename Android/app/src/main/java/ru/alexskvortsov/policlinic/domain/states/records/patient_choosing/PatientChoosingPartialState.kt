package ru.alexskvortsov.policlinic.domain.states.records.patient_choosing

import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

sealed class PatientChoosingPartialState(private val logMessage: String) {

    data class Loading(val flag: Boolean) : PatientChoosingPartialState("Loading $flag")
    data class Error(val e: Throwable) : PatientChoosingPartialState("Error $e")

    data class PatientsLoaded(val list: List<PatientEntity>) : PatientChoosingPartialState("PatientsLoaded $list")
    data class ValueChanged(val text: String) : PatientChoosingPartialState("ValueChanges $text")
    data class FilterChanged(val newFilter: PatientChoosingViewState.Filter) : PatientChoosingPartialState("FilterChanges $newFilter")

    override fun toString(): String = logMessage
    fun partial() = this
}