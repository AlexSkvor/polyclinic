package ru.alexskvortsov.policlinic.data.storage.unpacking

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import io.reactivex.Completable
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.RegistryStaffDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.UserDao
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.RegistryStaffEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity
import ru.alexskvortsov.policlinic.toothpick.DefaultFilesDir
import java.io.File
import javax.inject.Inject

class UsersUnpacker @Inject constructor(
    private val userDao: UserDao,
    private val doctorDao: DoctorDao,
    private val registryDao: RegistryStaffDao,
    private val patientDao: PatientDao,
    private val context: Context,
    private val gson: Gson,
    @DefaultFilesDir private val filesDir: String
) {

    fun unpack(): Completable = Completable.fromAction {
        unpackDoctors()
        unpackPatients()
        unpackRegistry()
    }

    private fun unpackDoctors() {
        val usersTest = getTextForRawResourceFile(R.raw.doctors_users)
        val users = gson.fromJson(usersTest, Array<UserEntity>::class.java).toList()
        userDao.insertAll(users)

        val doctorsText = getTextForRawResourceFile(R.raw.doctors)
        val doctors = gson.fromJson(doctorsText, Array<DoctorEntity>::class.java).toList()
        doctorDao.insertAll(doctors)
    }

    private fun unpackPatients() {
        val usersTest = getTextForRawResourceFile(R.raw.patients_users)
        val users = gson.fromJson(usersTest, Array<UserEntity>::class.java).toList()
        userDao.insertAll(users)

        val patientsText = getTextForRawResourceFile(R.raw.patients)
        val patients = gson.fromJson(patientsText, Array<PatientEntity>::class.java).toList()
        patientDao.insertAll(patients)
    }

    private fun unpackRegistry() {
        val usersTest = getTextForRawResourceFile(R.raw.registry_users)
        val users = gson.fromJson(usersTest, Array<UserEntity>::class.java).toList()
        userDao.insertAll(users)

        val registryText = getTextForRawResourceFile(R.raw.registry)
        val registry = gson.fromJson(registryText, Array<RegistryStaffEntity>::class.java).toList()
        registryDao.insertAll(registry)
    }

    private fun getTextForRawResourceFile(@RawRes raw: Int): String {
        val rawFile = context.resources.openRawResource(raw)
        val tempFile = File("$filesDir/tempFile.json")
        if (tempFile.exists()) tempFile.deleteRecursively()
        tempFile.createNewFile()

        rawFile.copyTo(tempFile.outputStream())
        return tempFile.readText()
    }

}