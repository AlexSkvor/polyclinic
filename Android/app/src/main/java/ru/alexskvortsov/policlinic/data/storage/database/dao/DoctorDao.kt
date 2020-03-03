package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import ru.alexskvortsov.policlinic.data.storage.entities.DoctorEntity

@Dao
interface DoctorDao : BaseDao<DoctorEntity> {
}