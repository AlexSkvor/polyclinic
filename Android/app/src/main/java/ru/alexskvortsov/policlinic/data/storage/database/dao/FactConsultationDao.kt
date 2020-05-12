package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.FactConsultationEntity

@Dao
interface FactConsultationDao : BaseDao<FactConsultationEntity> {

    @Query("SELECT * FROM fact_consultations WHERE consultationId = :planId")
    fun getFactConsultationForPlan(planId: String): Single<FactConsultationEntity>

}