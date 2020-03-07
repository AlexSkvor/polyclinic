package ru.alexskvortsov.policlinic.ui.utils.delegate

import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.valueIterator
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable

open class CompositeDelegateAdapter<T>(
    private val typeToAdapterMap: SparseArray<DelegateAdapterProvider<RecyclerView.ViewHolder, T>>,
    val actions: Observable<UserAction<T>>,
    private val data: MutableList<T> = arrayListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        typeToAdapterMap.valueIterator()
                .forEach { adapter -> adapter.notifyItemChanged = { notifyItemChanged(it) } }
    }

    fun getItemByPos(position: Int): T? =
            if (position >= data.size) null
            else data[position]

    fun getItemPosition(condition: (T) -> Boolean): Int =
            data.indexOfFirst { condition(it) }

    fun getItemOrNull(condition: (T) -> Boolean): T? =
            data.firstOrNull { condition(it) }

    override fun getItemViewType(position: Int): Int {
        for (i in 0 until typeToAdapterMap.size()) {
            if (typeToAdapterMap.valueAt(i).isForViewType(data, position))
                return typeToAdapterMap.keyAt(i)
        }
        throw NullPointerException("Can not get viewType for position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return typeToAdapterMap.get(viewType).onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adapter = typeToAdapterMap.get(getItemViewType(position))
                ?: throw NullPointerException("Can not find adapter for position $position")
        adapter.onBindViewHolder(holder, data, position)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        typeToAdapterMap.get(holder.itemViewType).onRecycled(holder)
    }

    open fun replaceData(newData: List<T>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    fun dataEquals(list: List<T>): Boolean = (list == data)

    companion object {
        class Builder<T>(private val typeToAdapterMap: SparseArray<DelegateAdapterProvider<RecyclerView.ViewHolder, T>> = SparseArray()) {
            private var count: Int = 0
            private val actionsList = mutableListOf<Observable<UserAction<T>>>()

            @Suppress("UNCHECKED_CAST")
            fun add(adapter: DelegateAdapter<out T>): Builder<T> =
                    this.also {
                        actionsList.add(adapter.getAction().map { it as UserAction<T> })
                        typeToAdapterMap.put(count, adapter as DelegateAdapterProvider<RecyclerView.ViewHolder, T>)
                        count++
                    }


            fun build(): CompositeDelegateAdapter<T> {
                require(count != 0) { "Register at least one adapter" }
                return CompositeDelegateAdapter(typeToAdapterMap, Observable.merge(actionsList))
            }
        }
    }
}