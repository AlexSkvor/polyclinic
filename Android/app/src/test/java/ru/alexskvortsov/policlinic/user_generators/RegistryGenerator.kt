package ru.alexskvortsov.policlinic.user_generators

import ru.alexskvortsov.policlinic.data.storage.database.entities.RegistryStaffEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.uuid
import kotlin.random.Random

class RegistryGenerator {

    fun generate(num: Int): Pair<List<RegistryStaffEntity>, List<UserEntity>> {
        val registry = mutableListOf<RegistryStaffEntity>()
        val users = mutableListOf<UserEntity>()
        for (i in 0..num) {
            val regPerson = newRegistry(Random.nextBoolean())
            registry.add(regPerson)
            users.add(regPerson.generatedUser(users))
        }

        return registry to users
    }

    private fun newRegistry(gender: Boolean): RegistryStaffEntity {
        return RegistryStaffEntity(
            id = uuid,
            surname = getSurname(gender),
            name = getName(gender),
            fathersName = getFathersName(gender),
            userId = uuid
        )
    }

}