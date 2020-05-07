package ru.alexskvortsov.policlinic.domain.states.doctor

import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity

sealed class DoctorProfilePartialState(private val log: String) {

    object DoctorSaved: DoctorProfilePartialState("PersonSaved")

    data class Loading(val flag: Boolean) : DoctorProfilePartialState("Loading $flag")
    data class Error(val e: Throwable) : DoctorProfilePartialState("Error $e")

    data class DoctorLoaded(val doctor: DoctorPerson) : DoctorProfilePartialState("PersonLoaded $doctor")
    data class LoginVerified(val unique: Boolean) : DoctorProfilePartialState("LoginCanBeSaved $unique")

    data class NewName(val name: String) : DoctorProfilePartialState("NewName $name")
    data class NewSurname(val surname: String) : DoctorProfilePartialState("NewSurname $surname")
    data class NewFathersName(val fathersName: String) : DoctorProfilePartialState("NewFathersName $fathersName")
    data class NewLogin(val login: String) : DoctorProfilePartialState("NewLogin $login")
    data class NewPhone(val phone: String) : DoctorProfilePartialState("NewPhone $phone")

    data class CompetenceLoaded(val list: List<CompetenceEntity>): DoctorProfilePartialState("CompetenceLoaded $list")
    data class CompetencesChange(val list: List<CompetenceEntity>): DoctorProfilePartialState("CompetencesChange $list")

    override fun toString(): String = log
    fun partial() = this
}