package ru.alexskvortsov.policlinic.ui.fragments.registry

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.registry_root_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.presentation.root.LogOutPresenter
import ru.alexskvortsov.policlinic.presentation.root.LogOutView
import ru.alexskvortsov.policlinic.setupLongText
import ru.alexskvortsov.policlinic.setupWithNavControllerReselectionDisabled
import ru.alexskvortsov.policlinic.ui.base.BaseMviFlowFragment

class RegistryRootFragment: BaseMviFlowFragment<LogOutView, LogOutPresenter>(), LogOutView {

    override val layoutRes: Int
        get() = R.layout.registry_root_fragment
    override val navHostId: Int
        get() = R.id.registryNavHostFragment

    override fun createPresenter(): LogOutPresenter = scope.getInstance(LogOutPresenter::class.java)

    private val actionExitEvent = BehaviorRelay.create<Unit>()
    override fun logOutIntent(): Observable<Unit> = actionExitEvent.hide()

    private val initLogout: PublishRelay<Unit> = PublishRelay.create()
    override fun initLogout(): Observable<Unit> = initLogout.hide()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as MviActivity<*, *>).setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        registry_bottom_navigation.setupLongText()
        registry_bottom_navigation.setupWithNavControllerReselectionDisabled(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_out_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.exitMenu -> {
                initLogout.accept(Unit)
                requireActivity().finishAndRemoveTask()
                true
            }
            R.id.exitUser -> {
                actionExitEvent.accept(Unit)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}