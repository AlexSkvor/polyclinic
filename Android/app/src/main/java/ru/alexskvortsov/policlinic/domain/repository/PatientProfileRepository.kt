package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.patient.PatientPerson

interface PatientProfileRepository {
    fun isLoginUnique(login: String): Observable<Boolean>
    fun getPatient(): Observable<PatientPerson>
    fun savePatient(person: PatientPerson): Completable
}