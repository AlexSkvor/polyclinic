package ru.alexskvortsov.policlinic.domain.states.patient

data class PatientPerson(
    val surname: String,
    val name: String,
    val fathersName: String,
    val login: String,
    val phone: String,
    val passportNumber: String,
    val omsPoliceNumber: String,
    val weight: String,
    val height: String,
    val snilsNumber: String,
    val berthDate: String,
    val gender: Boolean, //true -> men
    val userId: String,
    val patientId: String
) {

    companion object {
        private val emptyInstance: PatientPerson by lazy {
            PatientPerson(
                passportNumber = "", snilsNumber = "", omsPoliceNumber = "", weight = "", height = "", name = "",
                surname = "", fathersName = "", login = "", gender = true, berthDate = "", userId = "", patientId = "", phone = ""
            )
        }

        fun empty(): PatientPerson = emptyInstance
    }

    fun isEmpty(): Boolean = this == emptyInstance
    fun isNotEmpty(): Boolean = !isEmpty()
}