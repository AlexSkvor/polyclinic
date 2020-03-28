package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.registry.profile.RegistryPerson

interface RegistryProfileRepository {
    fun getPerson(): Observable<RegistryPerson>
    fun isLoginUnique(login: String): Observable<Boolean>
    fun savePerson(person: RegistryPerson): Completable
}