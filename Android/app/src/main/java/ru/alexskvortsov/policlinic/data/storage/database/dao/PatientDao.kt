package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

@Dao
interface PatientDao: BaseDao<PatientEntity> {
}