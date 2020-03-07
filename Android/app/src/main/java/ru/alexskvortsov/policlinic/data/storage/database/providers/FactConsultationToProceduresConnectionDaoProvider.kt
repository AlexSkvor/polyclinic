package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.FactConsultationToProceduresConnectionDao
import javax.inject.Inject
import javax.inject.Provider

class FactConsultationToProceduresConnectionDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<FactConsultationToProceduresConnectionDao> {
    override fun get(): FactConsultationToProceduresConnectionDao = database.factConsultationToProceduresConnectionDao()
}