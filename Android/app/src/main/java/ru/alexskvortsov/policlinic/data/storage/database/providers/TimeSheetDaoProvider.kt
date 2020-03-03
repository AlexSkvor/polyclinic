package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.TimeSheetDao
import javax.inject.Inject
import javax.inject.Provider

class TimeSheetDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<TimeSheetDao> {
    override fun get(): TimeSheetDao = database.timeSheetDao()
}