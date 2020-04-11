package ru.alexskvortsov.policlinic.domain.states.doctor

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.repository.DoctorProfileRepository
import ru.alexskvortsov.policlinic.domain.toObservableWithDefault
import javax.inject.Inject

class DoctorProfileInteractor @Inject constructor(
    private val repository: DoctorProfileRepository
) {

    fun loadDoctor(): Observable<DoctorProfilePartialState> = repository.getDoctor()
        .map { DoctorProfilePartialState.DoctorLoaded(it).partial() }
        .startWith(DoctorProfilePartialState.Loading(true))
        .onErrorReturn { DoctorProfilePartialState.Error(it) }
        .endWith(DoctorProfilePartialState.Loading(false))

    fun verifyLogin(login: String): Observable<DoctorProfilePartialState> = repository.isLoginUnique(login)
        .map { DoctorProfilePartialState.LoginVerified(it).partial() }
        .onErrorReturn { DoctorProfilePartialState.Error(it) }

    fun saveDoctor(doctor: DoctorPerson): Observable<DoctorProfilePartialState> = repository.saveDoctor(doctor)
        .toObservableWithDefault(DoctorProfilePartialState.DoctorSaved.partial())
        .concatWith(loadDoctor())
        .startWith(DoctorProfilePartialState.Loading(true))
        .onErrorReturn { DoctorProfilePartialState.Error(it) }
        .endWith(DoctorProfilePartialState.Loading(false))
}