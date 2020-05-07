package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity

@Dao
interface CompetenceDao: BaseDao<CompetenceEntity> {

    @Query("SELECT * FROM competencies WHERE id in (SELECT competenceId FROM doctor_to_competence_connections WHERE doctorId = :doctorId)")
    fun getByDoctorId(doctorId: String): Single<List<CompetenceEntity>>

    @Query("SELECT * FROM competencies")
    fun getAll(): Single<List<CompetenceEntity>>

    @Query("DELETE FROM doctor_to_competence_connections WHERE doctorId = :doctorId")
    fun clearForDoctor(doctorId: String): Completable
}