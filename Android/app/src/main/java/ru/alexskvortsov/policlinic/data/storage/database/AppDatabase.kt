package ru.alexskvortsov.policlinic.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.DoctorDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.PatientDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.RegistryStaffDao
import ru.alexskvortsov.policlinic.data.storage.database.dao.TimeSheetDao
import ru.alexskvortsov.policlinic.data.storage.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.entities.RegistryStaffEntity
import ru.alexskvortsov.policlinic.data.storage.entities.TimeSheetEntity

@Database(
    entities = [
        DoctorEntity::class,
        PatientEntity::class,
        RegistryStaffEntity::class,
        TimeSheetEntity::class
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doctorDao(): DoctorDao
    abstract fun patientDao(): PatientDao
    abstract fun registryStaffDao(): RegistryStaffDao
    abstract fun timeSheetDao(): TimeSheetDao
}