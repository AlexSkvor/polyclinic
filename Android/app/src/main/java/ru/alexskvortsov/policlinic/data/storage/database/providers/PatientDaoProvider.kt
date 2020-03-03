package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import javax.inject.Inject
import javax.inject.Provider

class PatientDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<PatientDao> {
    override fun get(): PatientDao = database.patientDao()
}