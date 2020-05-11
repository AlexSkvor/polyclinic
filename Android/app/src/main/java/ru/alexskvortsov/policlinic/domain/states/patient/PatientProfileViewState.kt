package ru.alexskvortsov.policlinic.domain.states.patient

import ru.alexskvortsov.policlinic.domain.filterDigits

data class PatientProfileViewState(
    val patient: PatientPerson = PatientPerson.empty(),
    val changedPatient: PatientPerson = patient,
    val loginUnique: Boolean = true,
    val verificationStarted: Boolean = false
) {

    val canSave: Boolean
        get() = (loginUnique && !verificationStarted) &&
                changedPatient.fathersName.isNotBlank() &&
                changedPatient.name.isNotBlank() &&
                changedPatient.surname.isNotBlank() &&
                changedPatient.login.isNotBlank() &&
                changedPatient.passportNumber.isValidPassportNumber() &&
                changedPatient.omsPoliceNumber.isValidOmsPoliceNumber() &&
                changedPatient.weight.toIntOrNull() in 10..500 &&
                changedPatient.height.toIntOrNull() in 50..300 &&
                changedPatient.phone.isValidPhone()

    val hasChanges: Boolean
        get() = patient != changedPatient

    val loginChanged: Boolean
        get() = patient.login != changedPatient.login

    private fun String.isValidPassportNumber(): Boolean =
        length == 11 && this[4] == ' ' && filterDigits().length == 10

    private fun String.isValidOmsPoliceNumber(): Boolean =
        length == 19 && this[4] == ' ' && this[9] == ' ' && this[14] == ' ' && filterDigits().length == 16

    private fun String.isValidPhone(): Boolean = when {
        isBlank() -> false
        length != 12 -> false
        first() == '+' -> (filterDigits().length == 11)
        else -> false
    }
}