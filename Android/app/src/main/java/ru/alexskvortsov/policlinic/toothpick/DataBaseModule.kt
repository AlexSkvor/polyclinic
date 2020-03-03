package ru.alexskvortsov.policlinic.toothpick

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.providers.AppDatabaseProvider
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.TimeSheetDao
import ru.alexskvortsov.policlinic.data.storage.database.providers.DoctorDaoProvider
import ru.alexskvortsov.policlinic.data.storage.database.providers.PatientDaoProvider
import ru.alexskvortsov.policlinic.data.storage.database.providers.TimeSheetDaoProvider
import toothpick.config.Module

class DataBaseModule : Module() {
    init {
        bind(AppDatabase::class.java).toProvider(AppDatabaseProvider::class.java).singleton()
        bind(DoctorDao::class.java).toProvider(DoctorDaoProvider::class.java).singleton()
        bind(PatientDao::class.java).toProvider(PatientDaoProvider::class.java).singleton()
        bind(TimeSheetDao::class.java).toProvider(TimeSheetDaoProvider::class.java).singleton()
    }
}