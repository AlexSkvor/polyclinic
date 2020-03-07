package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.UserDao
import javax.inject.Inject
import javax.inject.Provider

class UserDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<UserDao> {
    override fun get(): UserDao = database.userDao()
}