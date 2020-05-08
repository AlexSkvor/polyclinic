package ru.alexskvortsov.policlinic.domain.states.doctor

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.repository.DoctorProfileRepository
import ru.alexskvortsov.policlinic.domain.toObservableWithDefault
import javax.inject.Inject

class DoctorProfileInteractor @Inject constructor(
    private val repository: DoctorProfileRepository
) {

    fun loadDoctor(): Observable<DoctorProfilePartialState> = loadDoctorOnly()
        .startWith(DoctorProfilePartialState.Loading(true))
        .concatWith(loadAllCompetences())
        .endWith(DoctorProfilePartialState.Loading(false))

    private fun loadDoctorOnly() = repository.getDoctor()
        .map { DoctorProfilePartialState.DoctorLoaded(it).partial() }
        .onErrorReturn { DoctorProfilePartialState.Error(it) }

    private fun loadAllCompetences() = repository.getAllCompetences()
        .map { DoctorProfilePartialState.CompetenceLoaded(it).partial() }
        .onErrorReturn { DoctorProfilePartialState.Error(it) }

    fun verifyLogin(login: String): Observable<DoctorProfilePartialState> = repository.isLoginUnique(login)
        .map { DoctorProfilePartialState.LoginVerified(it).partial() }
        .onErrorReturn { DoctorProfilePartialState.Error(it) }

    fun saveDoctor(doctor: DoctorPerson): Observable<DoctorProfilePartialState> = repository.saveDoctor(doctor)
        .toObservableWithDefault(DoctorProfilePartialState.DoctorSaved.partial())
        .startWith(DoctorProfilePartialState.Loading(true))
        .onErrorReturn { DoctorProfilePartialState.Error(it) }
        .concatWith(loadDoctorOnly())
        .endWith(DoctorProfilePartialState.Loading(false))
}