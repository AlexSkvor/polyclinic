package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorToCompetenceConnectionEntity

@Dao
interface DoctorToCompetenceConnectionDao : BaseDao<DoctorToCompetenceConnectionEntity> {
}