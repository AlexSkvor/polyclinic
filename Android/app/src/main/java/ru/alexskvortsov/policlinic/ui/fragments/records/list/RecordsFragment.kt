package ru.alexskvortsov.policlinic.ui.fragments.records.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.material.selections
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.records_fragment.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.service.RecordsService
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.domain.repository.RecordsRepository
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo
import ru.alexskvortsov.policlinic.domain.states.records.record_info.RecordHost
import ru.alexskvortsov.policlinic.domain.states.records.record_info.UpdateListNotifier
import ru.alexskvortsov.policlinic.domain.states.records.list.Record
import ru.alexskvortsov.policlinic.domain.states.records.list.RecordsViewState
import ru.alexskvortsov.policlinic.presentation.records.list.RecordsPresenter
import ru.alexskvortsov.policlinic.presentation.records.list.RecordsView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment
import ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation.BigPastConsultationFragment
import ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation.DoctorCreatingConsultationInfo
import ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation.FutureConsultationFragment
import ru.alexskvortsov.policlinic.ui.fragments.records.detail_consultation.SmallPastConsultationFragment
import ru.alexskvortsov.policlinic.ui.fragments.records.recording.RecordingDialogFragment
import ru.alexskvortsov.policlinic.ui.utils.CardRecyclerDecoration
import ru.alexskvortsov.policlinic.ui.utils.delegate.CompositeDelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.pressedItems
import toothpick.Scope
import toothpick.config.Module

class RecordsFragment : BaseMviFragment<RecordsView, RecordsPresenter>(), RecordsView {

    override val layoutRes: Int
        get() = R.layout.records_fragment

    override fun createPresenter(): RecordsPresenter = fromScope()

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(RecordsRepository::class.java).to(RecordsService::class.java)
                bind(UpdateListNotifier::class.java).toInstance(
                    UpdateListNotifier()
                )
                bind(RecordHost::class.java).toInstance(
                    RecordHost()
                )
            }
        })
    }

    private val recordHost by lazy { fromScope<RecordHost>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startRecordingButton.setOnClickListener { startRecording() }

        recyclerConsultation.layoutManager = LinearLayoutManager(requireContext())
        val space = resources.getDimensionPixelSize(R.dimen.marginExtraLarge)
        recyclerConsultation.addItemDecoration(CardRecyclerDecoration(space))
        recyclerConsultation.adapter = recordsAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        recordsAdapter.actions.pressedItems()
            .subscribe { openDetailRecordDialog(it) }.bind()
    }

    override fun reloadIntent(): Observable<Unit> = needReloadRelay.hide()
    private val needReloadRelay = PublishRelay.create<Unit>()
    private fun startRecording() {
        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(RECORDING_DIALOG_TAG)
        if (fragment == null) {
            val recordingFragment = RecordingDialogFragment.newInstance(scope.name.toString()) {
                needReloadRelay.accept(Unit)
            }
            recordingFragment.show(requireActivity().supportFragmentManager, RECORDING_DIALOG_TAG)
            requireActivity().supportFragmentManager.executePendingTransactions()
        }
    }

    companion object {
        const val RECORDING_DIALOG_TAG = "RECORDING_DIALOG_TAG"
    }

    private val recordsAdapter: CompositeDelegateAdapter<Record> by lazy {
        CompositeDelegateAdapter.Companion.Builder<Record>()
            .add(RecordsAdapter())
            .build()
    }

    override fun typeChangeIntent(): Observable<RecordsViewState.ListType> = tabsListType.selections()
        .filter { it.position in 0..1 }
        .map { if (it.position == 0) RecordsViewState.ListType.FUTURE else RecordsViewState.ListType.PAST }

    override fun render(state: RecordsViewState) {
        renderListType(state.listType)
        renderList(state.list)
    }

    private fun renderListType(type: RecordsViewState.ListType) {
        val currentType = if (tabsListType.selectedTabPosition == 0) RecordsViewState.ListType.FUTURE
        else RecordsViewState.ListType.PAST

        if (currentType != type) {
            val tab = tabsListType.getTabAt(if (type == RecordsViewState.ListType.FUTURE) 0 else 1)
            tabsListType.selectTab(tab)
        }
    }

    private fun renderList(list: List<Record>) {
        if (!recordsAdapter.dataEquals(list))
            recordsAdapter.replaceData(list)
    }

    private val prefs by lazy { fromScope<AppPrefs>() }

    private fun openDetailRecordDialog(record: Record) {
        recordHost.record = record
        if (record.passed) openPastDetailRecordDialog()
        else openFutureDetailRecordDialog(record)
    }

    private fun openPastDetailRecordDialog() {
        when (prefs.currentUser.type) {
            UserAuthInfo.UserType.DOCTOR -> openBigPastRecord()
            UserAuthInfo.UserType.REGISTRY -> openSmallPastRecord()
            UserAuthInfo.UserType.PATIENT -> openBigPastRecord()
        }
    }

    private fun openFutureDetailRecordDialog(record: Record) {
        val userId = prefs.currentUser.userId
        when (prefs.currentUser.type) {
            UserAuthInfo.UserType.DOCTOR -> if (record.doctorEntity.userId == userId) openFutureRecordForDoctor()
            else openFutureRecord()
            UserAuthInfo.UserType.REGISTRY -> openFutureRecord()
            UserAuthInfo.UserType.PATIENT -> openFutureRecord()
        }
    }

    /***
     * Big -> can cancel for future, can start if doctor, can see results
     * Small -> can cancel for future, nothing more
     */
    private fun openFutureRecordForDoctor() {
        val fragment = DoctorCreatingConsultationInfo.newInstance(scope.name.toString())
        fragment.show(requireActivity().supportFragmentManager, DoctorCreatingConsultationInfo.TAG)
        requireActivity().supportFragmentManager.executePendingTransactions()
    }

    private fun openFutureRecord() {
        val fragment = FutureConsultationFragment.newInstance(scope.name.toString())
        fragment.show(requireActivity().supportFragmentManager, FutureConsultationFragment.TAG)
        requireActivity().supportFragmentManager.executePendingTransactions()
    }

    private fun openBigPastRecord() {
        val fragment = BigPastConsultationFragment.newInstance(scope.name.toString())
        fragment.show(requireActivity().supportFragmentManager, BigPastConsultationFragment.TAG)
        requireActivity().supportFragmentManager.executePendingTransactions()
    }

    private fun openSmallPastRecord() {
        val fragment = SmallPastConsultationFragment.newInstance(scope.name.toString())
        fragment.show(requireActivity().supportFragmentManager, SmallPastConsultationFragment.TAG)
        requireActivity().supportFragmentManager.executePendingTransactions()
    }
}