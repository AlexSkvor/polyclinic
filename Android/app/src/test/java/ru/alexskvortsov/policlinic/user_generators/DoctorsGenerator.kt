package ru.alexskvortsov.policlinic.user_generators

import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.uuid
import kotlin.random.Random

class DoctorsGenerator {

    companion object {
        private val professionsList = listOf(
            "Врач высшей категории",
            "Врач",
            "Доктор медицинских наук",
            "Кандидат медицинских наук",
            "Врач первой категории",
            "Врач второй категории"
        )
    }

    fun generate(num: Int): Pair<List<DoctorEntity>, List<UserEntity>> {
        val doctors = mutableListOf<DoctorEntity>()
        val users = mutableListOf<UserEntity>()
        for (i in 0..num) {
            val doctor = newDoctor(Random.nextBoolean())
            doctors.add(doctor)
            users.add(doctor.generatedUser(users))
        }

        return doctors to users
    }

    private fun newDoctor(gender: Boolean): DoctorEntity {
        return DoctorEntity(
            id = uuid,
            professionName = professionsList.random(),
            workExperienceYears = Random.nextInt(1, 10),
            educationMainDocumentRef = "",
            surname = getSurname(gender),
            name = getName(gender),
            fathersName = getFathersName(gender),
            gender = gender,
            berthDate = getBerthDate(),
            phone = getPhone(),
            userId = uuid
        )
    }

}