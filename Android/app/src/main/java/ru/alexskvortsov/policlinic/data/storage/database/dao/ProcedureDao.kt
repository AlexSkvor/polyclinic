package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity

@Dao
interface ProcedureDao : BaseDao<ProcedureEntity> {

    @Query("SELECT * FROM procedures")
    fun getAll(): Single<List<ProcedureEntity>>

}