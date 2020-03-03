package ru.alexskvortsov.policlinic.data.storage.database.providers

import android.content.Context
import androidx.room.Room
import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import javax.inject.Inject
import javax.inject.Provider

class AppDatabaseProvider @Inject constructor(private val context: Context): Provider<AppDatabase> {
    override fun get(): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
}