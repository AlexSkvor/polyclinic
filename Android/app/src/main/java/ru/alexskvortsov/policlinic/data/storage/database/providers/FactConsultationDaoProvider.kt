package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.FactConsultationDao
import javax.inject.Inject
import javax.inject.Provider

class FactConsultationDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<FactConsultationDao> {
    override fun get(): FactConsultationDao = database.factConsultationDao()
}