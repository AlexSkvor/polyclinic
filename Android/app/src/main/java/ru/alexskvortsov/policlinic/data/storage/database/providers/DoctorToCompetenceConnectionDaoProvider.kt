package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorToCompetenceConnectionDao
import javax.inject.Inject
import javax.inject.Provider

class DoctorToCompetenceConnectionDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<DoctorToCompetenceConnectionDao> {
    override fun get(): DoctorToCompetenceConnectionDao = database.doctorToCompetenceConnectionDao()
}