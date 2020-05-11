package ru.alexskvortsov.policlinic.domain.states.patient

sealed class PatientProfilePartialState(private val log: String) {

    object PatientSaved : PatientProfilePartialState("PersonSaved")

    data class Loading(val flag: Boolean) : PatientProfilePartialState("Loading $flag")
    data class Error(val e: Throwable) : PatientProfilePartialState("Error $e")

    data class PersonLoaded(val person: PatientPerson) : PatientProfilePartialState("PersonLoaded $person")
    data class LoginVerified(val unique: Boolean) : PatientProfilePartialState("LoginCanBeSaved $unique")

    data class NewName(val name: String) : PatientProfilePartialState("NewName $name")
    data class NewSurname(val surname: String) : PatientProfilePartialState("NewSurname $surname")
    data class NewFathersName(val fathersName: String) : PatientProfilePartialState("NewFathersName $fathersName")
    data class NewLogin(val login: String) : PatientProfilePartialState("NewLogin $login")
    data class NewPhone(val phone: String): PatientProfilePartialState("NewPhone $phone")

    data class NewPassportNumber(val passportNumber: String) : PatientProfilePartialState("NewPassportNumber $passportNumber")
    data class NewOmsNumber(val omsNumber: String) : PatientProfilePartialState("NewOmsNumber $omsNumber")

    data class NewHeight(val height: String) : PatientProfilePartialState("NewHeight $height")
    data class NewWeight(val weight: String) : PatientProfilePartialState("NewWeight $weight")

    override fun toString(): String = log
    fun partial() = this
}