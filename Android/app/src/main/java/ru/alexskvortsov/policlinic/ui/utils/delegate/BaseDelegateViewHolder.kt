package ru.alexskvortsov.policlinic.ui.utils.delegate

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseDelegateViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {

    lateinit var listener: (view: View, item: Any) -> Unit

    fun bind(item: Any) = listener(itemView, item)
}