package ru.alexskvortsov.policlinic.domain.states.records.patient_choosing

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.filterDigits
import ru.alexskvortsov.policlinic.domain.repository.PatientChoosingRepository
import javax.inject.Inject

class PatientChoosingInteractor @Inject constructor(
    private val repository: PatientChoosingRepository
) {

    fun valueChanged(filter: PatientChoosingViewState.Filter, value: String): Observable<PatientChoosingPartialState> =
        when (filter) {
            PatientChoosingViewState.Filter.Passport -> passportFilter(value)
            PatientChoosingViewState.Filter.Phone -> phoneFilter(value)
            PatientChoosingViewState.Filter.Oms -> omsFilter(value)
            PatientChoosingViewState.Filter.Snils -> snilsFilter(value)
            PatientChoosingViewState.Filter.Surname -> surnameFilter(value)
        }.startWith(PatientChoosingPartialState.ValueChanged(value))

    private fun passportFilter(value: String): Observable<PatientChoosingPartialState> =
        if (!value.isValidPassportNumber()) Observable.empty()
        else repository.getPatientsByPassport(value).standardEnding()

    private fun phoneFilter(value: String): Observable<PatientChoosingPartialState> {
        val filteredValuePhone = value.filter { it in '0'..'9' || it == '+' }
        return if (!filteredValuePhone.isValidPhone()) Observable.empty()
        else repository.getPatientsByPhone(filteredValuePhone).standardEnding()
    }

    private fun omsFilter(value: String): Observable<PatientChoosingPartialState> =
        if (!value.isValidOmsPoliceNumber()) Observable.empty()
        else repository.getPatientsByOms(value).standardEnding()

    private fun snilsFilter(value: String): Observable<PatientChoosingPartialState> =
        if (!value.isValidSnilsNumber()) Observable.empty()
        else repository.getPatientsBySnils(value).standardEnding()

    private fun surnameFilter(value: String): Observable<PatientChoosingPartialState> =
        if (value.isBlank()) Observable.empty()
        else repository.getPatientsBySurname(value).standardEnding()

    private fun String.isValidPassportNumber(): Boolean =
        length == 11 && this[4] == ' ' && filterDigits().length == 10

    private fun String.isValidOmsPoliceNumber(): Boolean =
        length == 19 && this[4] == ' ' && this[9] == ' ' && this[14] == ' ' && filterDigits().length == 16

    private fun String.isValidSnilsNumber(): Boolean =
        length == 14 && this[3] == ' ' && this[7] == ' ' && this[11] == ' ' && filterDigits().length == 11

    private fun String.isValidPhone(): Boolean = when {
        isBlank() -> false
        length != 12 -> false
        first() == '+' -> (filterDigits().length == 11)
        else -> false
    }

    private fun Observable<List<PatientEntity>>.standardEnding() = this
        .map { PatientChoosingPartialState.PatientsLoaded(it).partial() }
        .startWith(PatientChoosingPartialState.Loading(true))
        .onErrorReturn { PatientChoosingPartialState.Error(it) }
        .endWith(PatientChoosingPartialState.Loading(false))

}