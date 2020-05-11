package ru.alexskvortsov.policlinic.domain.states.doctor

import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.domain.filterDigits

data class DoctorProfileViewState(
    val doctor: DoctorPerson = DoctorPerson.empty(),
    val changedDoctor: DoctorPerson = doctor,
    val possibleCompetences: List<CompetenceEntity> = emptyList(),
    val loginUnique: Boolean = true,
    val verificationStarted: Boolean = false
) {

    val canSave: Boolean
        get() = (loginUnique && !verificationStarted) &&
                changedDoctor.fathersName.isNotBlank() &&
                changedDoctor.name.isNotBlank() &&
                changedDoctor.surname.isNotBlank() &&
                changedDoctor.login.isNotBlank() &&
                changedDoctor.phone.isValidPhone()

    val hasChanges: Boolean
        get() = doctor != changedDoctor

    val loginChanged: Boolean
        get() = doctor.login != changedDoctor.login

    private fun String.isValidPhone(): Boolean = when {
        isBlank() -> false
        length != 12 -> false
        first() == '+' -> (filterDigits().length == 11)
        else -> false
    }
}