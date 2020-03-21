package ru.alexskvortsov.policlinic.user_generators

import com.google.gson.Gson
import java.io.File

class UsersGenerator {

    private val gson = Gson()

    fun generate(num: Int) {
        generateDoctors(num)
        generatePatients(num * 10)
        generateRegistry(num)
    }

    private fun generateDoctors(num: Int) {
        val (doctors, users) = DoctorsGenerator().generate(num)

        val doctorsFile = File("doctors.json")
        doctorsFile.createNewFile()
        doctorsFile.writeText(gson.toJson(doctors))

        val usersFile = File("doctors_users.json")
        usersFile.createNewFile()
        usersFile.writeText(gson.toJson(users))
    }

    private fun generatePatients(num: Int) {
        val (patients, users) = PatientsGenerator().generate(num)

        val patientsFile = File("patients.json")
        patientsFile.createNewFile()
        patientsFile.writeText(gson.toJson(patients))

        val usersFile = File("patients_users.json")
        usersFile.createNewFile()
        usersFile.writeText(gson.toJson(users))
    }

    private fun generateRegistry(num: Int) {
        val (registry, users) = RegistryGenerator().generate(num)

        val registryFile = File("registry.json")
        registryFile.createNewFile()
        registryFile.writeText(gson.toJson(registry))

        val usersFile = File("registry_users.json")
        usersFile.createNewFile()
        usersFile.writeText(gson.toJson(users))
    }

}