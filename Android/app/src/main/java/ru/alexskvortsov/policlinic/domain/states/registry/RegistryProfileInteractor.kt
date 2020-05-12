package ru.alexskvortsov.policlinic.domain.states.registry

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.repository.RegistryProfileRepository
import ru.alexskvortsov.policlinic.domain.toObservableWithDefault
import javax.inject.Inject

class RegistryProfileInteractor @Inject constructor(
    private val repository: RegistryProfileRepository
) {

    fun loadPerson(): Observable<RegistryProfilePartialState> = repository.getPerson()
        .map { RegistryProfilePartialState.PersonLoaded(it).partial() }
        .startWith(RegistryProfilePartialState.Loading(true))
        .onErrorReturn { RegistryProfilePartialState.Error(it) }
        .endWith(RegistryProfilePartialState.Loading(false))

    fun verifyLogin(login: String): Observable<RegistryProfilePartialState> = repository.isLoginUnique(login)
        .map { RegistryProfilePartialState.LoginVerified(it).partial() }
        .onErrorReturn { RegistryProfilePartialState.Error(it) }

    fun savePerson(person: RegistryPerson): Observable<RegistryProfilePartialState> = repository.savePerson(person)
        .toObservableWithDefault(RegistryProfilePartialState.PersonSaved.partial())
        .concatWith(loadPerson())
        .startWith(RegistryProfilePartialState.Loading(true))
        .onErrorReturn { RegistryProfilePartialState.Error(it) }
        .endWith(RegistryProfilePartialState.Loading(false))
}