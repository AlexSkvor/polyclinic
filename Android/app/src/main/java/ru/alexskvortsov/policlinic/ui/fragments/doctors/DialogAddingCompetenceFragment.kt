package ru.alexskvortsov.policlinic.ui.fragments.doctors

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity

/*class DialogAddingCompetenceFragment : DialogFragment() {

    companion object {
        fun newInstance(list: List<CompetenceEntity>, onChosenListener: (CompetenceEntity) -> Unit): DialogAddingCompetenceFragment =
            DialogAddingCompetenceFragment()
                .also {
                    it.listOfCompetences = list
                    it.onChosen = onChosenListener
                }
    }

    private lateinit var inflatedView: View
    val layoutRes: Int = R.layout.dialog_fragment_sync_progress

    private lateinit var listOfCompetences: List<CompetenceEntity>
    private lateinit var onChosen: (CompetenceEntity) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutRes, container, true)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inflatedView = requireActivity().layoutInflater.inflate(layoutRes, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.sync_string)
            .setView(inflatedView)
            .setCancelable(true)
            .create()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}*/