package ru.alexskvortsov.policlinic.ui.utils.delegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable

interface DelegateAdapterProvider<VH : RecyclerView.ViewHolder, T> {
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: VH, items: List<T>, position: Int)
    fun onRecycled(holder: VH)
    fun isForViewType(items: List<*>, position: Int): Boolean
    fun getAction(): Observable<UserAction<T>>
    var notifyItemChanged: (Int) -> Unit
}