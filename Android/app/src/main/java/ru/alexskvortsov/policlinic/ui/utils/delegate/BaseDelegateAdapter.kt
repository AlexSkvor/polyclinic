package ru.alexskvortsov.policlinic.ui.utils.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseDelegateAdapter<VH : BaseDelegateViewHolder, T> :
    DelegateAdapterProvider<VH, T> {
    protected abstract fun onBindViewHolder(view: View, item: T, holder: VH)

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun createViewHolder(parent: View): VH

    override fun onRecycled(holder: VH) {}

    override var notifyItemChanged: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
                .inflate(getLayoutId(), parent, false)
        val holder = createViewHolder(inflatedView)
        holder.listener = { view, item ->
            @Suppress("UNCHECKED_CAST")
            onBindViewHolder(view, item as T, holder)
        }
        return holder
    }

    override fun onBindViewHolder(holder: VH, items: List<T>, position: Int) {
        holder.bind(items[position] as Any)
    }
}