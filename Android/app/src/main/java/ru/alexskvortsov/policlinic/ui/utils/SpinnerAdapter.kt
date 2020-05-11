package ru.alexskvortsov.policlinic.ui.utils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.spinner_item.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.utils.SpinnerItem

open class SpinnerAdapter(context: Context,
                          private val changeTextSize: Boolean = false,
                          private val hasHint: Boolean = false,
                          items: List<SpinnerItem> = mutableListOf()
) : ArrayAdapter<SpinnerItem>(context, R.layout.spinner_item, R.id.spinnerTextView, items.toMutableList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: View.inflate(context, R.layout.spinner_item, null)
        view.spinnerTextView.maxLines = 1
        val text = getItem(position)?.nameSpinner ?: ""
        if (text.length > 20 && changeTextSize) {
            view.spinnerTextView.textSize = 12F
        }
        view.spinnerTextView.text = text
        view.spinnerTextView.setTextColor(Color.WHITE)

        return view
    }

    override fun getCount(): Int {
        val count = super.getCount()
        return if (hasHint && count > 0) count - 1 else count
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.spinner_item, parent, false)
        view.spinnerTextView.text = getItem(position)?.nameSpinner

        return view
    }

    var currentItems: List<SpinnerItem> = listOf()

    fun replace(list: List<SpinnerItem>) {
        if (list != currentItems) {
            super.clear()
            currentItems = list
            super.addAll(list)
        }
    }
}