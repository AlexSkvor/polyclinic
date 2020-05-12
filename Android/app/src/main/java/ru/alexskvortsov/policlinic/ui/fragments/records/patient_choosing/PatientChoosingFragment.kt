package ru.alexskvortsov.policlinic.ui.fragments.records.patient_choosing

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.itemSelections
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import kotlinx.android.synthetic.main.patient_choosing_fragment.view.*
import ru.alexskvortsov.policlinic.*
import ru.alexskvortsov.policlinic.data.service.PatientChoosingService
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.domain.repository.PatientChoosingRepository
import ru.alexskvortsov.policlinic.domain.states.records.patient_choosing.PatientChoosingViewState
import ru.alexskvortsov.policlinic.presentation.records.patient_choosing.PatientChoosingPresenter
import ru.alexskvortsov.policlinic.presentation.records.patient_choosing.PatientChoosingView
import ru.alexskvortsov.policlinic.toothpick.DI
import ru.alexskvortsov.policlinic.ui.base.BaseMviDialogFragment
import ru.alexskvortsov.policlinic.ui.utils.CardRecyclerDecoration
import ru.alexskvortsov.policlinic.ui.utils.SpinnerAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.CompositeDelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.pressedItems
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.slots.Slot
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import toothpick.Scope
import toothpick.config.Module
import java.util.concurrent.TimeUnit

class PatientChoosingFragment : BaseMviDialogFragment<PatientChoosingView, PatientChoosingPresenter>(),
    PatientChoosingView {

    companion object {
        fun newInstance(parentScope: String? = null, patientChosen: (String) -> Unit): PatientChoosingFragment {
            return PatientChoosingFragment().apply {
                arguments = Bundle().withArgument(PARENT_SCOPE_NAME, parentScope ?: DI.APP_SCOPE)
                patientSelectedListener = patientChosen
            }
        }
    }

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(PatientChoosingRepository::class.java).to(PatientChoosingService::class.java)
            }
        })
    }

    private var patientSelectedListener: ((String) -> Unit)? = null

    private lateinit var filterAdapter: SpinnerAdapter

    private lateinit var patientsAdapter: CompositeDelegateAdapter<PatientEntity>

    override val layoutRes: Int
        get() = R.layout.patient_choosing_fragment

    override fun createPresenter(): PatientChoosingPresenter = fromScope()

    override fun filterChangesIntent(): Observable<PatientChoosingViewState.Filter> = inflatedView.spinnerFilterType.itemSelections()
        .filter { inflatedView.spinnerFilterType.selectedItem != null }
        .map { inflatedView.spinnerFilterType.selectedItem as PatientChoosingViewState.Filter }
        .distinctUntilChanged()

    override fun valueChangesIntent(): Observable<String> = inflatedView.filterPatientChoosing.changes()

    override fun render(state: PatientChoosingViewState) = withRender {
        renderTypes(state.possibleFilters, state.chosenFilter)
        when (state.chosenFilter) {
            PatientChoosingViewState.Filter.Phone -> updateMask(phoneMask, getString(R.string.phoneHint))
            PatientChoosingViewState.Filter.Oms -> updateMask(omsMask, getString(R.string.hintOms))
            PatientChoosingViewState.Filter.Snils -> updateMask(snilsMask, getString(R.string.hintSnils))
            PatientChoosingViewState.Filter.Passport -> updateMask(passportMask, getString(R.string.hintPassport))
            PatientChoosingViewState.Filter.Surname -> updateMask(null, getString(R.string.hintSurname), false)
        }
        renderEditTextField(state.interredValue, inflatedView.filterPatientChoosing)
        renderPatientsList(state.patientsList)
    }

    private fun renderPatientsList(patients: List<PatientEntity>) {
        if (!patientsAdapter.dataEquals(patients))
            patientsAdapter.replaceData(patients)
    }

    private fun renderEditTextField(text: String, view: TextInputEditText) {
        view.setTextIfNotEqual(text)
        view.inputLayout?.isErrorEnabled = text.isBlank()
    }

    private fun renderTypes(filters: List<PatientChoosingViewState.Filter>, selectedType: PatientChoosingViewState.Filter?) {
        filterAdapter.replace(filters)
        if (selectedType != null)
            inflatedView.spinnerFilterType.setSelection(filterAdapter.getPosition(selectedType))
    }

    private var prevMask: MaskFormatWatcher? = null

    private fun updateMask(mask: MaskFormatWatcher?, hint: String, numeric: Boolean = true) {
        inflatedView.filterPatientChoosing.inputType = if (numeric) InputType.TYPE_CLASS_PHONE
        else InputType.TYPE_CLASS_TEXT
        if (mask == null || mask != prevMask) {
            prevMask = mask
            masksList.forEach {
                inflatedView.filterPatientChoosing.removeTextChangedListener(it)
            }
            mask?.installOn(inflatedView.filterPatientChoosing)
        }
        inflatedView.filterPatientChoosingLayout.hint = hint
    }

    private fun getWatcher(slots: Array<out Slot>): MaskFormatWatcher {
        val mask = MaskImpl.createTerminated(slots)
        mask.isForbidInputWhenFilled = true
        return MaskFormatWatcher(mask)
    }

    private val phoneMask by lazy { getWatcher(PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (___) ___ - ____")) }
    private val omsMask by lazy { getWatcher(UnderscoreDigitSlotsParser().parseSlots("____ ____ ____ ____")) }
    private val snilsMask by lazy { getWatcher(UnderscoreDigitSlotsParser().parseSlots("___ ___ ___ __")) }
    private val passportMask by lazy { getWatcher(UnderscoreDigitSlotsParser().parseSlots("____ ______")) }

    private val masksList by lazy { listOf(phoneMask, omsMask, snilsMask, passportMask) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        filterAdapter = SpinnerAdapter(requireContext(), changeTextSize = true)
        patientsAdapter = CompositeDelegateAdapter.Companion.Builder<PatientEntity>()
            .add(PatientsChoosingAdapter())
            .build()

        patientsAdapter.actions.pressedItems()
            .subscribe {
                patientSelectedListener?.invoke(it.id)
                dismiss()
            }.bind()
    }

    private lateinit var inflatedView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inflatedView = requireActivity().layoutInflater.inflate(layoutRes, null)

        inflatedView.spinnerFilterType.adapter = filterAdapter

        inflatedView.patientsRecycler.layoutManager = LinearLayoutManager(requireContext())
        val space = resources.getDimensionPixelSize(R.dimen.marginExtraLarge)
        inflatedView.patientsRecycler.addItemDecoration(
            CardRecyclerDecoration(
                space
            )
        )
        inflatedView.patientsRecycler.adapter = patientsAdapter

        return AlertDialog.Builder(requireContext(), R.style.DialogTheme)
            .setView(inflatedView)
            .setCancelable(true)
            .create()
    }

    private fun TextInputEditText.changes() = this.textChanges()
        .skipInitialValue()
        .filter { !isRender }
        .map { it.toString() }
        .uiDebounce(300, TimeUnit.MILLISECONDS)
}