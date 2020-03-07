package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.WorkShiftDao
import javax.inject.Inject
import javax.inject.Provider

class WorkShiftDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<WorkShiftDao> {
    override fun get(): WorkShiftDao = database.workShiftDao()
}