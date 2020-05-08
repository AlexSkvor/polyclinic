package ru.alexskvortsov.policlinic.domain.states.patient

import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.endWith
import ru.alexskvortsov.policlinic.domain.repository.PatientProfileRepository
import ru.alexskvortsov.policlinic.domain.toObservableWithDefault
import javax.inject.Inject

class PatientProfileInteractor @Inject constructor(
    private val repository: PatientProfileRepository
) {

    fun loadPatient(): Observable<PatientProfilePartialState> = loadPatientOnly()
        .startWith(PatientProfilePartialState.Loading(true))
        .endWith(PatientProfilePartialState.Loading(false))

    private fun loadPatientOnly() = repository.getPatient()
        .map { PatientProfilePartialState.PersonLoaded(it).partial() }
        .onErrorReturn { PatientProfilePartialState.Error(it) }

    fun verifyLogin(login: String): Observable<PatientProfilePartialState> = repository.isLoginUnique(login)
        .map { PatientProfilePartialState.LoginVerified(it).partial() }
        .onErrorReturn { PatientProfilePartialState.Error(it) }

    fun savePatient(patient: PatientPerson): Observable<PatientProfilePartialState> = repository.savePatient(patient)
        .toObservableWithDefault(PatientProfilePartialState.PatientSaved.partial())
        .startWith(PatientProfilePartialState.Loading(true))
        .onErrorReturn { PatientProfilePartialState.Error(it) }
        .concatWith(loadPatientOnly())
        .endWith(PatientProfilePartialState.Loading(false))
}