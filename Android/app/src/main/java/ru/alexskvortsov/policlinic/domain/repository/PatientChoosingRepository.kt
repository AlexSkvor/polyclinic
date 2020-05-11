package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity

interface PatientChoosingRepository {
    fun getPatientsByPassport(value: String): Observable<List<PatientEntity>>
    fun getPatientsByPhone(value: String): Observable<List<PatientEntity>>
    fun getPatientsByOms(value: String): Observable<List<PatientEntity>>
    fun getPatientsBySnils(value: String): Observable<List<PatientEntity>>
    fun getPatientsBySurname(value: String): Observable<List<PatientEntity>>
}