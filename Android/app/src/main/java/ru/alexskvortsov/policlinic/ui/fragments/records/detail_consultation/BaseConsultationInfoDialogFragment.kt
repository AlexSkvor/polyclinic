package ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Observable.just
import kotlinx.android.synthetic.main.consultations_detail_fragment.view.*
import ru.alexskvortsov.policlinic.*
import ru.alexskvortsov.policlinic.data.service.CancelConsultationService
import ru.alexskvortsov.policlinic.data.service.FactConsultationService
import ru.alexskvortsov.policlinic.data.storage.database.entities.DoctorEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.PatientEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity
import ru.alexskvortsov.policlinic.data.storage.database.entities.strBerthDate
import ru.alexskvortsov.policlinic.domain.defaultIfNull
import ru.alexskvortsov.policlinic.domain.repository.CancelConsultationRepository
import ru.alexskvortsov.policlinic.domain.repository.FactConsultationRepository
import ru.alexskvortsov.policlinic.domain.states.records.record_info.ConsultationInfoViewState
import ru.alexskvortsov.policlinic.domain.states.records.list.Record
import ru.alexskvortsov.policlinic.presentation.records.record_info.ConsultationInfoPresenter
import ru.alexskvortsov.policlinic.presentation.records.record_info.ConsultationInfoView
import ru.alexskvortsov.policlinic.ui.base.BaseMviDialogFragment
import ru.alexskvortsov.policlinic.ui.utils.delegate.CompositeDelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.deletedItems
import ru.alexskvortsov.policlinic.ui.utils.delegate.pressedItems
import toothpick.Scope
import toothpick.config.Module
import java.util.concurrent.TimeUnit

abstract class BaseConsultationInfoDialogFragment : BaseMviDialogFragment<ConsultationInfoView, ConsultationInfoPresenter>(),
    ConsultationInfoView {

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(FactConsultationRepository::class.java).to(FactConsultationService::class.java)
                bind(CancelConsultationRepository::class.java).to(CancelConsultationService::class.java)
            }
        })
    }

    override val layoutRes: Int
        get() = R.layout.consultations_detail_fragment

    override fun createPresenter(): ConsultationInfoPresenter = fromScope()

    protected lateinit var inflatedView: View
    abstract fun childRender(state: ConsultationInfoViewState)
    abstract fun setParentScopeName(parentScopeName: String?)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inflatedView = requireActivity().layoutInflater.inflate(layoutRes, null)

        setListenersForTitles()
        setDangerousButtonsListeners()
        setupRecycler()

        return AlertDialog.Builder(requireContext(), R.style.DialogTheme)
            .setView(inflatedView)
            .setCancelable(false)
            .create()
    }

    override fun initialIntent(): Observable<Unit> = just(Unit)

    override fun startIntent(): Observable<Unit> = inflatedView.startConsultationButton.clicks()

    private val undoRelay = PublishRelay.create<Unit>()
    override fun undoAllIntent(): Observable<Unit> = undoRelay.hide()

    private val saveRelay = PublishRelay.create<Unit>()
    override fun saveIntent(): Observable<Unit> = saveRelay.hide()

    private val cancelRelay = PublishRelay.create<Unit>()
    override fun cancelIntent(): Observable<Unit> = cancelRelay.hide()

    //TODO filter for state started in presenter!
    override fun addProcedureIntent(): Observable<ProcedureEntity> = proceduresAdapter.actions.pressedItems()
    override fun deleteProcedureIntent(): Observable<ProcedureEntity> = proceduresAdapter.actions.deletedItems()

    override fun noteChangedIntent(): Observable<String> = inflatedView.additionalNotesFactRecord.textChanges()
        .skipInitialValue()
        .filter { !isRender }
        .map { it.toString() }
        .uiDebounce(300, TimeUnit.MILLISECONDS)

    private var selectedProcedures = listOf<ProcedureEntity>()

    private val proceduresAdapter: CompositeDelegateAdapter<ProcedureEntity> by lazy {
        CompositeDelegateAdapter.Companion.Builder<ProcedureEntity>()
            .add(ProceduresAdapter { it in selectedProcedures })
            .build()
    }

    override fun render(state: ConsultationInfoViewState) = withRender {
        if (state.shouldCloseAndReload) dismiss()

        inflatedView.setBackgroundColor(getColor(if (state.record.cancelled) R.color.palette_red_transparent else R.color.transparent))

        renderPatientInfo(state.changedRecord.patientEntity)
        renderDoctorInfo(state.changedRecord.doctorEntity)
        renderRecordInfo(state.changedRecord)
        renderFactRecordInfo(state.changedRecord)

        selectedProcedures = state.record.proceduresUsed.orEmpty()
        if (!proceduresAdapter.dataEquals(state.possibleProcedures))
            proceduresAdapter.replaceData(state.possibleProcedures)

        childRender(state)
    }

    private fun renderPatientInfo(patient: PatientEntity) {
        inflatedView.surnamePatientProfile.setTextIfNotEqual(patient.surname)
        inflatedView.namePatientProfile.setTextIfNotEqual(patient.name)
        inflatedView.fathersNamePatientProfile.setTextIfNotEqual(patient.fathersName)
        inflatedView.phonePatientProfile.setTextIfNotEqual(patient.phoneNumber)
        inflatedView.passportPatientProfile.setTextIfNotEqual(patient.passportNumber)
        inflatedView.omsPatientProfile.setTextIfNotEqual(patient.omsPoliceNumber)
        inflatedView.weightPatientProfile.setTextIfNotEqual(patient.weight.toString())
        inflatedView.heightPatientProfile.setTextIfNotEqual(patient.height.toString())
        inflatedView.snilsPatientProfile.setTextIfNotEqual(patient.snilsNumber)
        inflatedView.berthDatePatientProfile.setTextIfNotEqual(patient.strBerthDate)
    }

    private fun renderDoctorInfo(doctor: DoctorEntity) {
        inflatedView.surnameDoctorProfile.setTextIfNotEqual(doctor.surname)
        inflatedView.nameDoctorProfile.setTextIfNotEqual(doctor.name)
        inflatedView.fathersNameDoctorProfile.setTextIfNotEqual(doctor.fathersName)
        inflatedView.phoneDoctorProfile.setTextIfNotEqual(doctor.phone)
        inflatedView.skillLevelDoctorProfile.setTextIfNotEqual(doctor.skillLevel)
        inflatedView.workExperienceYearsDoctorProfile.setTextIfNotEqual(doctor.workExperienceYears.toString())
    }

    private fun renderRecordInfo(record: Record) {
        inflatedView.competenceNameRecord.setTextIfNotEqual(record.competenceEntity.name)
        inflatedView.whoCreatedNameRecord.setTextIfNotEqual(record.createdUserEntity.login)
        inflatedView.startDateTimeRecord.setTextIfNotEqual(record.startTimePlan.format(formatterUiDateTime))
        inflatedView.reasonRecord.setTextIfNotEqual(record.reason)
    }

    private fun renderFactRecordInfo(record: Record) {
        inflatedView.startDateTimeFactRecord.setTextIfNotEqual(record.startTimeFact?.format(formatterUiDateTime) ?: "")
        inflatedView.endDateTimeFactRecord.setTextIfNotEqual(
            record.endTimeFact?.format(formatterUiDateTime).defaultIfNull(getString(R.string.notPassed))
        )
        inflatedView.additionalNotesFactRecord.setTextIfNotEqual(record.doctorNotes.orEmpty())
    }

    private fun setListenersForTitles() {
        inflatedView.titlePatientProfile.setOnClickListener {
            inflatedView.patientInfo.visible = !inflatedView.patientInfo.visible
        }

        inflatedView.titleDoctorProfile.setOnClickListener {
            inflatedView.doctorInfo.visible = !inflatedView.doctorInfo.visible
        }

        inflatedView.titleRecord.setOnClickListener {
            inflatedView.recordInfo.visible = !inflatedView.recordInfo.visible
        }

        inflatedView.titleFactRecord.setOnClickListener {
            inflatedView.factRecordInfo.visible = !inflatedView.factRecordInfo.visible
        }
    }

    private fun setupRecycler() {
        inflatedView.recyclerProceduresUsed.layoutManager = LinearLayoutManager(requireContext())
        inflatedView.recyclerProceduresUsed.adapter = proceduresAdapter
    }

    private fun setDangerousButtonsListeners() {
        inflatedView.undoButton.setOnClickListener {
            showAlert(getString(R.string.undoAlertMessage)) { undoRelay.accept(Unit) }
        }
        inflatedView.saveButton.setOnClickListener {
            showAlert(getString(R.string.saveAlertMessage)) { saveRelay.accept(Unit) }
        }
        inflatedView.cancelConsultationButton.setOnClickListener {
            showAlert(getString(R.string.cancelAlertMessage)) { cancelRelay.accept(Unit) }
        }
        inflatedView.closeButton.setOnClickListener { dismiss() }
    }

    private fun showAlert(message: String, onAgree: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.agreementDialogTitle)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.confirm) { _, _ -> onAgree() }
            .setNegativeButton(R.string.cancelDialogButton) { _, _ -> }
            .create().show()
    }
}