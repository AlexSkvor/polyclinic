package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.RegistryStaffEntity

@Dao
interface RegistryStaffDao : BaseDao<RegistryStaffEntity> {

    @Query("SELECT * FROM registry_staffs")
    fun getAllRegistryList(): Single<List<RegistryStaffEntity>>

    @Query("SELECT * FROM registry_staffs WHERE userId = :userId")
    fun getByUserId(userId: String): Single<RegistryStaffEntity>

    @Query("SELECT COUNT(*) FROM registry_staffs WHERE userId = :userId")
    fun existsWithUserId(userId: String): Int
}