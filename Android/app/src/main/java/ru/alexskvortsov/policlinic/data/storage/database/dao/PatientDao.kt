package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

@Dao
interface PatientDao: BaseDao<PatientEntity> {

    @Query("SELECT * FROM patients")
    fun getAllPatientsList(): Single<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE userId = :userId")
    fun getByUserId(userId: String): Single<PatientEntity>

    @Query("SELECT COUNT(*) FROM patients WHERE userId = :userId")
    fun existsWithUserId(userId: String): Int
}