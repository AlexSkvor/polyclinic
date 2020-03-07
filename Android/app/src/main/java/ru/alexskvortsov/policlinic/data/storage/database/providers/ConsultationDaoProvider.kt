package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.ConsultationDao
import javax.inject.Inject
import javax.inject.Provider

class ConsultationDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<ConsultationDao> {
    override fun get(): ConsultationDao = database.consultationDao()
}