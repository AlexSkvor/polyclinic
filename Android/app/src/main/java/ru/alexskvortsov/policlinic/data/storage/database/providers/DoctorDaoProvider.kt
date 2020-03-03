package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorDao
import javax.inject.Inject
import javax.inject.Provider

class DoctorDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<DoctorDao> {
    override fun get(): DoctorDao = database.doctorDao()
}