package ru.alexskvortsov.policlinic.data.storage.database.providers

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.AttachedDocumentDao
import javax.inject.Inject
import javax.inject.Provider

class AttachedDocumentDaoProvider @Inject constructor(private val database: AppDatabase) :
    Provider<AttachedDocumentDao> {
    override fun get(): AttachedDocumentDao = database.attachedDocumentDao()
}