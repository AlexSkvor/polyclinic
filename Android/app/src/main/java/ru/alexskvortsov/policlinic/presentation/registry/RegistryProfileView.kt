package ru.alexskvortsov.policlinic.presentation.registry

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.states.registry.profile.RegistryProfileViewState

interface RegistryProfileView : MvpView {

    fun initIntent(): Observable<Unit>

    fun nameChangedIntent(): Observable<String>
    fun surnameChangedIntent(): Observable<String>
    fun fathersNameChangedIntent(): Observable<String>
    fun loginChangedIntent(): Observable<String>

    fun saveIntent(): Observable<Unit>
    fun cancelIntent(): Observable<Unit>

    fun render(state: RegistryProfileViewState)
}