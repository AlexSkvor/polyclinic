package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.CompetenceDao
import javax.inject.Inject
import javax.inject.Provider

class CompetenceDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<CompetenceDao> {
    override fun get(): CompetenceDao = database.competenceDao()
}