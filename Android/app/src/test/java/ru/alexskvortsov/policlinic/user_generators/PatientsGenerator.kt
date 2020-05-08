package ru.alexskvortsov.policlinic.user_generators

import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.uuid
import kotlin.random.Random

class PatientsGenerator {

    fun generate(num: Int): Pair<List<PatientEntity>, List<UserEntity>> {
        val patients = mutableListOf<PatientEntity>()
        val users = mutableListOf<UserEntity>()
        for (i in 0..num) {
            val patient = newPatient(Random.nextBoolean(), patients)
            patients.add(patient)
            users.add(patient.generatedUser(users))
        }

        return patients to users
    }

    private fun newPatient(gender: Boolean, exists: List<PatientEntity>): PatientEntity {
        return PatientEntity(
            id = uuid,
            passportNumber = getPassportNumber(exists.map { it.passportNumber }),
            snilsNumber = getSnilsNumber(exists.map { it.snilsNumber }),
            omsPoliceNumber = getOmsNumber(exists.map { it.omsPoliceNumber }),
            name = getName(gender),
            surname = getSurname(gender),
            fathersName = getFathersName(gender),
            gender = gender,
            berthDate = getBerthDate(),
            weight = Random.nextInt(45, 150),
            height = Random.nextInt(150, 220),
            phoneNumber = getPhone(),
            userId = uuid
        )
    }

    private fun getPassportNumber(exists: List<String>): String {

        fun newPassport(): String {
            val series = Random.nextInt(1000, 10000).toString()
            val number = Random.nextInt(100000, 1000000).toString()
            return "$series $number"
        }

        var passportNumber = newPassport()
        while (passportNumber in exists) passportNumber = newPassport()
        return passportNumber
    }

    private fun getSnilsNumber(exists: List<String>): String {
        fun newSnils(): String {
            fun part(maxExclusive: Int = 1000): String {
                return Random.nextInt(maxExclusive / 10, maxExclusive).toString()
            }
            return "${part()} ${part()} ${part()} ${part(100)}"
        }

        var snils = newSnils()
        while (snils in exists) snils = newSnils()
        return snils
    }

    private fun getOmsNumber(exists: List<String>): String {
        fun newOms(): String {
            fun part(maxExclusive: Int = 10000): String {
                return Random.nextInt(maxExclusive / 10, maxExclusive).toString()
            }
            return "${part()} ${part()} ${part()} ${part()}"
        }

        var oms = newOms()
        while (oms in exists) oms = newOms()
        return oms
    }

}