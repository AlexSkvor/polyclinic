package ru.alexskvortsov.policlinic.presentation.base

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.alexskvortsov.policlinic.domain.utils.AppDisposables
import ru.alexskvortsov.policlinic.domain.utils.DisposablesProvider

abstract class BaseMviPresenter<V : MvpView, VM> : MviBasePresenter<V, VM>(), DisposablesProvider by AppDisposables() {
    override fun unbindIntents() {
        super.unbindIntents()
        clear()
    }

    protected val lastState: Observable<VM> get() = viewStateObservable.take(1)
    protected fun <T, VH> Observable<VH>.switchMapWithLastState(block: VM.(VH) -> Observable<T>): Observable<T> {
        return this.switchMap { data ->
            withLastState {
                block(data)
            }
        }
    }

    protected fun <T> withLastState(block: VM.() -> Observable<T>): Observable<T> {
        return lastState.switchMap {
            block(it)
        }
    }
}