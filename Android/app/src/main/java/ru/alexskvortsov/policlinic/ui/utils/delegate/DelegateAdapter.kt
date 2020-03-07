package ru.alexskvortsov.policlinic.ui.utils.delegate

import android.view.View
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.android.extensions.LayoutContainer

abstract class DelegateAdapter<T> : BaseDelegateAdapter<DelegateAdapter.DelegateViewHolder, T>() {

    override fun getAction(): Observable<UserAction<T>> =
            Completable.complete().toObservable()

    abstract fun onBind(item: T, holder: DelegateViewHolder)

    final override fun onBindViewHolder(view: View, item: T, holder: DelegateViewHolder) {
        onBind(item, holder)
    }

    override fun createViewHolder(parent: View): DelegateViewHolder {
        return DelegateViewHolder(parent)
    }

    class DelegateViewHolder(override val containerView: View) : BaseDelegateViewHolder(containerView), LayoutContainer
}