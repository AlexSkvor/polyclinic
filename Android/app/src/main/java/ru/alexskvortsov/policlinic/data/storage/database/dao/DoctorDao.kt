package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity

@Dao
interface DoctorDao : BaseDao<DoctorEntity> {

    @Query("SELECT * FROM doctors")
    fun getAllDoctorsList(): Single<List<DoctorEntity>>

}