package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.RegistryStaffDao
import javax.inject.Inject
import javax.inject.Provider

class RegistryStaffDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<RegistryStaffDao> {
    override fun get(): RegistryStaffDao = database.registryStaffDao()
}