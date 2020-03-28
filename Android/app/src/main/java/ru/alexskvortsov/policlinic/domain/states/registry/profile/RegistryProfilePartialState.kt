package ru.alexskvortsov.policlinic.domain.states.registry.profile

sealed class RegistryProfilePartialState(val log: String) {

    object PersonSaved: RegistryProfilePartialState("PersonSaved")

    data class Loading(val flag: Boolean) : RegistryProfilePartialState("Loading $flag")
    data class Error(val e: Throwable) : RegistryProfilePartialState("Error $e")

    data class PersonLoaded(val person: RegistryPerson) : RegistryProfilePartialState("PersonLoaded $person")
    data class LoginVerified(val unique: Boolean) : RegistryProfilePartialState("LoginCanBeSaved $unique")

    data class NewName(val name: String) : RegistryProfilePartialState("NewName $name")
    data class NewSurname(val surname: String) : RegistryProfilePartialState("NewSurname $surname")
    data class NewFathersName(val fathersName: String) : RegistryProfilePartialState("NewFathersName $fathersName")
    data class NewLogin(val login: String) : RegistryProfilePartialState("NewLogin $login")

    fun partial() = this
    override fun toString(): String = log
}