package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.ProcedureDao
import javax.inject.Inject
import javax.inject.Provider

class ProcedureDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<ProcedureDao> {
    override fun get(): ProcedureDao = database.procedureDao()
}