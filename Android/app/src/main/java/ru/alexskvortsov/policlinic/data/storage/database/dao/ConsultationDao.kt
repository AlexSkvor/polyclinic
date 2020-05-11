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

}