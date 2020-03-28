package ru.alexskvortsov.policlinic.domain.states.registry.profile

data class RegistryProfileViewState(
    val person: RegistryPerson = RegistryPerson.empty(),
    val changedPerson: RegistryPerson = person,
    val loginUnique: Boolean = true,
    val verificationStarted: Boolean = false
) {
    val canSave: Boolean
        get() = (loginUnique && !verificationStarted) &&
                changedPerson.fathersName.isNotBlank() &&
                changedPerson.name.isNotBlank() &&
                changedPerson.surname.isNotBlank() &&
                changedPerson.login.isNotBlank()

    val hasChanges: Boolean
        get() = person != changedPerson

    val loginChanged: Boolean
        get() = person.login != changedPerson.login
}