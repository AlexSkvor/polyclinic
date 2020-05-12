package ru.alexskvortsov.policlinic.domain.states.records.patient_choosing

import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.domain.utils.SpinnerItem

data class PatientChoosingViewState(
    val chosenFilter: Filter = Filter.Passport,
    val interredValue: String = "",
    val patientsList: List<PatientEntity> = emptyList()
) {

    val possibleFilters
        get() = Filter.possibleFiltersCompanion

    sealed class Filter(
        override val nameSpinner: String,
        override val uuid: String = ""
    ) : SpinnerItem {
        object Phone : Filter("Телефон")
        object Passport : Filter("Номер Пасспорта")
        object Oms : Filter("Номер ОМС")
        object Snils : Filter("Номер СНИЛС")
        object Surname : Filter("Фамилия")

        companion object {
            val possibleFiltersCompanion: List<Filter> by lazy { listOf(Passport, Phone, Oms, Snils, Surname) }
        }
    }
}