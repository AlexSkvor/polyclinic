package ru.alexskvortsov.policlinic.domain.states.doctor

import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity

data class DoctorPerson(
    val name: String,
    val surname: String,
    val fathersName: String,
    val login: String,
    val skillLevel: String,
    val workExperienceYears: Int,
    val educationMainDocumentRef: String,
    val gender: Boolean, //true -> men
    val berthDate: String,
    val phone: String,
    val competenceList: List<CompetenceEntity>,
    val userId: String,
    val doctorId: String
) {

    companion object {
        fun empty() = DoctorPerson("", "", "", "", "", -1, "", true, "", "", emptyList(), "", "")
    }

    fun isEmpty() = this == empty()
}