package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

@Dao
interface PatientDao : BaseDao<PatientEntity> {

    @Query("SELECT * FROM patients")
    fun getAllPatientsList(): Single<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE userId = :userId")
    fun getByUserId(userId: String): Single<PatientEntity>

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getById(id: String): Single<PatientEntity>

    @Query("SELECT * FROM patients WHERE passportNumber = :passportNumber")
    fun getByPassport(passportNumber: String): Single<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE omsPoliceNumber = :omsNumber")
    fun getByOms(omsNumber: String): Single<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE phoneNumber = :phone")
    fun getByPhone(phone: String): Single<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE snilsNumber = :snils")
    fun getBySnils(snils: String): Single<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE surname = :surname")
    fun getBySurname(surname: String): Single<List<PatientEntity>>

    @Query("SELECT COUNT(*) FROM patients WHERE userId = :userId")
    fun existsWithUserId(userId: String): Int

    @Query(
        "UPDATE patients SET " +
                "surname = :surname, " +
                "name = :name, " +
                "fathersName = :fathersName, " +
                "passportNumber = :passportNumber, " +
                "omsPoliceNumber = :omsPoliceNumber, " +
                "weight = :weight, height = :height, " +
                "phoneNumber = :phone " +
                "WHERE id = :patientId"
    )
    fun updatePatient(
        patientId: String, surname: String, name: String, fathersName: String,
        passportNumber: String, omsPoliceNumber: String, weight: Int, height: Int, phone: String
    ): Completable
}