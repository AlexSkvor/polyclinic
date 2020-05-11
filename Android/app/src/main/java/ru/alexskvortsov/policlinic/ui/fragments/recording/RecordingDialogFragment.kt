package ru.alexskvortsov.policlinic.ui.fragments.recording

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.itemSelections
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Observable.just
import kotlinx.android.synthetic.main.recording_fragment.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.*
import ru.alexskvortsov.policlinic.data.service.RecordingService
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.domain.repository.RecordingRepository
import ru.alexskvortsov.policlinic.domain.states.recording.RecordingViewState
import ru.alexskvortsov.policlinic.domain.utils.SpinnerItem
import ru.alexskvortsov.policlinic.presentation.recording.RecordingPresenter
import ru.alexskvortsov.policlinic.presentation.recording.RecordingView
import ru.alexskvortsov.policlinic.toothpick.DI
import ru.alexskvortsov.policlinic.ui.base.BaseMviDialogFragment
import ru.alexskvortsov.policlinic.ui.utils.CardRecyclerDecoration
import ru.alexskvortsov.policlinic.ui.utils.SpinnerAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.CompositeDelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.pressedItems
import toothpick.Scope
import toothpick.config.Module
import java.util.concurrent.TimeUnit

class RecordingDialogFragment : BaseMviDialogFragment<RecordingView, RecordingPresenter>(), RecordingView {

    companion object {
        fun newInstance(parentScope: String? = null, itemCreated: (String) -> Unit): RecordingDialogFragment {
            return RecordingDialogFragment().apply {
                arguments = Bundle().withArgument(PARENT_SCOPE_NAME, parentScope ?: DI.APP_SCOPE)
                itemCreatedListener = itemCreated
            }
        }

        private const val PATIENT_CHOOSING_DIALOG_TAG: String = "PATIENT_CHOOSING_DIALOG_TAG"
    }

    private var itemCreatedListener: ((String) -> Unit)? = null

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(RecordingRepository::class.java).to(RecordingService::class.java)
            }
        })
    }

    override val layoutRes: Int
        get() = R.layout.recording_fragment

    private lateinit var inflatedView: View

    private val prefs by lazy { fromScope<AppPrefs>() }

    override fun createPresenter(): RecordingPresenter = fromScope()

    private val notSelected = object : SpinnerItem {
        override val uuid: String
            get() = ""
        override val nameSpinner: String
            get() = "Специализация"
    }

    private val notSelectedDoctor = object : SpinnerItem {
        override val uuid: String
            get() = ""
        override val nameSpinner: String
            get() = "Специалист"
    }

    private val patientRelay = PublishRelay.create<String>()
    override fun patientIntent(): Observable<String> {
        return if (prefs.currentUser.isPatient()) just(prefs.currentUser.realId)
        else patientRelay.hide()
    }

    override fun competenceIntent(): Observable<String> = inflatedView.spinnerCompetences.itemSelections()
        .filter { it > 0 }
        .map { inflatedView.spinnerCompetences.selectedItem as CompetenceEntity }
        .map { it.id }

    private val dateRelay = PublishRelay.create<LocalDateTime>()
    override fun dateIntent(): Observable<LocalDateTime> = dateRelay.hide()

    override fun doctorIntent(): Observable<String> = inflatedView.spinnerDoctors.itemSelections()
        .filter { it > 0 }
        .map { inflatedView.spinnerDoctors.selectedItem as DoctorEntity }
        .map { it.id }

    override fun startTimeIntent(): Observable<LocalDateTime> = timeAdapter.actions.pressedItems()
        .distinctUntilChanged()

    override fun reasonIntent(): Observable<String> = inflatedView.reasonText.changes()
    override fun createRecordIntent(): Observable<Unit> = buttonRelay.hide().take(1)

    override fun render(state: RecordingViewState) = withRender {
        if (state.recordCreatedId.isNotEmpty()) closeSelf(state.recordCreatedId)
        setupChoosePatientButton(state.patientEntity, !prefs.currentUser.isPatient())
        renderCompetencesList(state.allCompetencesList, state.competenceEntity, state.patientEntity != null)
        renderChosenDate(state.date, state.competenceEntity != null)
        renderDoctorsList(state.allDoctorsList, state.doctorEntity, state.date != null)
        renderStartTimes(state.allStartTimes, state.startTime, state.doctorEntity != null)
        renderReasonField(state.reason, state.startTime != null)
        renderSaveButton(state.reason.isNotBlank())
    }


    private val buttonRelay = PublishRelay.create<Unit>()
    private fun renderSaveButton(hasReason: Boolean) {
        inflatedView.createRecordButton.isEnabled = hasReason

        if (!hasReason) inflatedView.createRecordButton.setTextColor(getColor(R.color.palette_gray))
        else inflatedView.createRecordButton.setTextColor(getColor(R.color.blueTextColor))

        inflatedView.createRecordButton.setOnClickListener {
            if (hasReason) buttonRelay.accept(Unit)
        }
    }

    private var chosenTime: LocalDateTime? = null
    private val timeAdapter: CompositeDelegateAdapter<LocalDateTime> by lazy {
        CompositeDelegateAdapter.Companion.Builder<LocalDateTime>()
            .add(TimeChoosingAdapter {
                chosenTime
            })
            .build()
    }

    private fun renderStartTimes(possibleStartTimes: List<LocalDateTime>, newTime: LocalDateTime?, doctorChosen: Boolean) {
        if (chosenTime != newTime) {
            chosenTime = newTime
            timeAdapter.notifyDataSetChanged()
        }
        if (!timeAdapter.dataEquals(possibleStartTimes))
            if (doctorChosen) timeAdapter.replaceData(possibleStartTimes)
            else timeAdapter.replaceData(emptyList())
    }

    private fun renderReasonField(reason: String, timeChosen: Boolean) {
        inflatedView.reasonLayout.isEnabled = timeChosen
        inflatedView.reasonText.setTextIfNotEqual(if (timeChosen) reason else "")
    }

    private fun renderChosenDate(dateTime: LocalDateTime?, competenceChosen: Boolean) {
        inflatedView.dateText.isEnabled = competenceChosen
        if (!competenceChosen) inflatedView.dateText.setTextColor(getColor(R.color.palette_gray))
        else inflatedView.dateText.setTextColor(getColor(R.color.blueTextColor))

        if (competenceChosen) {
            if (dateTime == null) inflatedView.dateText.text = getString(R.string.dateNotChosen)
            else inflatedView.dateText.text = getString(R.string.dateChosen, dateTime.strRepresentation())
            inflatedView.dateText.setOnClickListener { openDateChoosingDialog() }
        } else {
            inflatedView.dateText.text = getString(R.string.dateNotChosen)
            inflatedView.dateText.setOnClickListener { }
        }
    }

    private fun Int.toTwoDigitsString(): String {
        return if (this < 10) "0$this"
        else this.toString()
    }

    private fun openDateChoosingDialog() {
        val now = LocalDateTime.now()
        val choosingFragment = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val str = "${dayOfMonth.toTwoDigitsString()}.${(month + 1).toTwoDigitsString()}.$year"
            val time = LocalDate.parse(str, formatterUiDate)
            dateRelay.accept(time.atStartOfDay())
        }, now.year, now.monthValue - 1, now.dayOfMonth)
        choosingFragment.show()
    }

    private fun LocalDateTime.strRepresentation() = this.format(formatterUiDate)

    private val competencesAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext(), changeTextSize = true) }
    private fun renderCompetencesList(list: List<CompetenceEntity>, selectedType: CompetenceEntity?, patientChosen: Boolean) {
        if (list.isEmpty() || !patientChosen) competencesAdapter.replace(listOf(notSelected))
        else competencesAdapter.replace(listOf(notSelected) + list)
        inflatedView.spinnerCompetences.isEnabled = list.isNotEmpty()

        if (selectedType != null)
            inflatedView.spinnerCompetences.setSelection(competencesAdapter.getPosition(selectedType))
    }

    private val doctorsAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext(), changeTextSize = true) }
    private fun renderDoctorsList(list: List<DoctorEntity>, selectedDoctor: DoctorEntity?, dateChosen: Boolean) {
        if (list.isEmpty() || !dateChosen) doctorsAdapter.replace(listOf(notSelectedDoctor))
        else doctorsAdapter.replace(listOf(notSelectedDoctor) + list)
        inflatedView.spinnerDoctors.isEnabled = list.isNotEmpty()

        if (selectedDoctor != null)
            inflatedView.spinnerDoctors.setSelection(doctorsAdapter.getPosition(selectedDoctor))
    }

    private fun setupChoosePatientButton(currentPatient: PatientEntity?, canChange: Boolean) {
        currentPatient?.let { inflatedView.patientFullName.text = it.fullName }
        if (canChange) inflatedView.patientFullName.setOnClickListener {
            openPatientChoosingDialog()
        }
    }

    private fun openPatientChoosingDialog() {
        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(PATIENT_CHOOSING_DIALOG_TAG)
        if (fragment == null) {
            val choosingFragment = PatientChoosingFragment.newInstance(scope.name.toString()) {
                patientRelay.accept(it)
            }
            choosingFragment.show(requireActivity().supportFragmentManager, PATIENT_CHOOSING_DIALOG_TAG)
            requireActivity().supportFragmentManager.executePendingTransactions()
        }
    }

    private fun closeSelf(recordId: String) {
        itemCreatedListener?.invoke(recordId)
        itemCreatedListener = null
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inflatedView = requireActivity().layoutInflater.inflate(layoutRes, null)

        inflatedView.recyclerTime.layoutManager = LinearLayoutManager(requireContext())
        val space = resources.getDimensionPixelSize(R.dimen.marginExtraLarge)
        inflatedView.recyclerTime.addItemDecoration(CardRecyclerDecoration(space))
        inflatedView.recyclerTime.adapter = timeAdapter

        inflatedView.spinnerDoctors.adapter = doctorsAdapter
        inflatedView.spinnerCompetences.adapter = competencesAdapter


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
