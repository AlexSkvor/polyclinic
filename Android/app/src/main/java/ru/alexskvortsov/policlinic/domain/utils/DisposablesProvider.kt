package ru.alexskvortsov.policlinic.domain.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface DisposablesProvider {
    val compositeDisposable: CompositeDisposable
    fun clear()
    fun Disposable.bind()
}