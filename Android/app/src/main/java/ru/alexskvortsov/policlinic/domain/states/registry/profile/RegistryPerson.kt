package ru.alexskvortsov.policlinic.domain.states.registry.profile

data class RegistryPerson(
    val name: String,
    val surname: String,
    val fathersName: String,
    val login: String,
    val userId: String,
    val registryId: String
){
    companion object{
        fun empty() = RegistryPerson("", "", "", "", "", "")
    }

    fun isEmpty() = this == empty()
}