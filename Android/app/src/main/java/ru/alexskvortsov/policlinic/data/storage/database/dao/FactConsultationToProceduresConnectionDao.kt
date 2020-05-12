package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.FactConsultationToProceduresConnectionEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity

@Dao
interface FactConsultationToProceduresConnectionDao : BaseDao<FactConsultationToProceduresConnectionEntity> {

    @Query("SELECT * FROM procedures WHERE id in (SELECT procedureId FROM fact_consultation_connection_to_procedures WHERE factConsultationId in (SELECT id FROM fact_consultations WHERE consultationId = :planId))")
    fun getProceduresForPlan(planId: String): Single<List<ProcedureEntity>>
}