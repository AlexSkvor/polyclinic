package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import org.threeten.bp.LocalDate
import ru.alexskvortsov.policlinic.data.storage.database.entities.ConsultationEntity
import ru.alexskvortsov.policlinic.formatterDB

@Dao
abstract class ConsultationDao : BaseDao<ConsultationEntity> {

    @Query("SELECT * FROM consultations_in_plan WHERE date = :date AND doctorId = :doctorId AND cancelled = :cancelled")
    abstract fun getByDateStringAndDoctorId(date: String, doctorId: String, cancelled: Boolean): Single<List<ConsultationEntity>>

    fun getByDateAndDoctorId(date: LocalDate, doctorId: String, cancelled: Boolean = false): Single<List<ConsultationEntity>> =
        getByDateStringAndDoctorId(date.atStartOfDay().format(formatterDB), doctorId, cancelled)

    @Query("SELECT * FROM consultations_in_plan WHERE (doctorId = :doctorId OR userAskedId = :userId) AND (cancelled = :t OR id in (SELECT consultationId FROM fact_consultations))")
    abstract fun getListForDoctorIdPast(doctorId: String, userId: String, t: Boolean = true): Single<List<ConsultationEntity>>

    @Query("SELECT * FROM consultations_in_plan WHERE (doctorId = :doctorId OR userAskedId = :userId) AND (NOT cancelled = :t) AND (NOT id in (SELECT consultationId FROM fact_consultations))")
    abstract fun getListForDoctorIdFuture(doctorId: String, userId: String, t: Boolean = true): Single<List<ConsultationEntity>>


    @Query("SELECT * FROM consultations_in_plan WHERE (patientId = :patientId OR userAskedId = :userId) AND (NOT cancelled = :t) AND (NOT id in (SELECT consultationId FROM fact_consultations))")
    abstract fun getListForPatientIdFuture(patientId: String, userId: String, t: Boolean = true): Single<List<ConsultationEntity>>

    @Query("SELECT * FROM consultations_in_plan WHERE (patientId = :patientId OR userAskedId = :userId) AND (cancelled = :t OR id in (SELECT consultationId FROM fact_consultations))")
    abstract fun getListForPatientIdPast(patientId: String, userId: String, t: Boolean = true): Single<List<ConsultationEntity>>


    @Query("SELECT * FROM consultations_in_plan WHERE userAskedId = :userId AND (NOT cancelled = :t) AND (NOT id in (SELECT consultationId FROM fact_consultations))")
    abstract fun getListForRegistryIdFuture(userId: String, t: Boolean = true): Single<List<ConsultationEntity>>

    @Query("SELECT * FROM consultations_in_plan WHERE userAskedId = :userId AND (cancelled = :t OR id in (SELECT consultationId FROM fact_consultations))")
    abstract fun getListForRegistryIdPast(userId: String, t: Boolean = true): Single<List<ConsultationEntity>>
}