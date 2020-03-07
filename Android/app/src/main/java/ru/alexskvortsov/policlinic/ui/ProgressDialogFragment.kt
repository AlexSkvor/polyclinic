package ru.alexskvortsov.policlinic.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import ru.alexskvortsov.policlinic.R

/**
 * Created on 4/25/2019
 * @author YWeber
 */
class ProgressDialogFragment : AppCompatDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setStyle(STYLE_NO_FRAME, R.style.ProgressDialogTheme)
        isCancelable = false
        return inflater.inflate(R.layout.dialog_progress, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext(), R.style.ProgressDialogTheme)
                .setView(R.layout.dialog_progress)
                .create()
    }
}