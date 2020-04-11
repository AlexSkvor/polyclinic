package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.doctor.DoctorPerson

interface DoctorProfileRepository {
    fun isLoginUnique(login: String): Observable<Boolean>
    fun getDoctor(): Observable<DoctorPerson>
    fun saveDoctor(person: DoctorPerson): Completable
}