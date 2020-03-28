package ru.alexskvortsov.policlinic.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.states.activity.AppViewState
import ru.alexskvortsov.policlinic.presentation.activity.AppPresenter
import ru.alexskvortsov.policlinic.presentation.activity.AppView
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationController
import ru.alexskvortsov.policlinic.presentation.navigation.Navigator
import ru.alexskvortsov.policlinic.toothpick.DI
import ru.alexskvortsov.policlinic.ui.base.BackPressedHandler
import ru.alexskvortsov.policlinic.ui.utils.ProgressDialogFragment
import ru.terrakok.cicerone.NavigatorHolder
import toothpick.Toothpick
import javax.inject.Inject

class MainActivity : MviActivity<AppView, AppPresenter>(), AppView {

    companion object {
        private const val DIALOG_TAG = "dialog fragment"
    }

    private val scope = Toothpick.openScope(DI.APP_SCOPE)
    override fun createPresenter(): AppPresenter = scope.getInstance(AppPresenter::class.java)

    private val restoreIntent: PublishRelay<Unit> = PublishRelay.create()
    override fun restore(): Observable<Unit> = restoreIntent.hide()
    private var restoringState: Boolean = false

    private val refreshIntent: PublishRelay<Unit> = PublishRelay.create()
    override fun refresh(): Observable<Unit> = refreshIntent.hide()

    @Inject
    lateinit var navigationHolder: NavigatorHolder

    private val navigatorController
        get() = NavigationController(Navigation.findNavController(this, R.id.mainNavHostFragment))

    private val navigator
        get() = Navigator(navigatorController)

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, scope)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun render(state: AppViewState) {
        when (state.render) {
            AppViewState.Render.RESTORE -> showProgress(state.progress)
            AppViewState.Render.TOAST -> showToast(state.toast)
            AppViewState.Render.SNACK_BAR -> showSnackbar(state.snackBar)
            AppViewState.Render.PROGRESS -> showProgress(state.progress)
            else -> {
            }
        }
        if (state.render != AppViewState.Render.NONE) refreshIntent.accept(Unit)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun showToast(message: String) {
        if (message.isNotBlank()) {
            val toastView = layoutInflater.inflate(R.layout.toast_lauout, null)
            (toastView as TextView).text = "\n  $message  \n"
            val toast = Toast(this)
            toast.setGravity(Gravity.TOP or Gravity.END, resources.getDimension(R.dimen.marginBig).toInt(), resources.getDimension(R.dimen.marginBig).toInt())
            toast.duration = Toast.LENGTH_LONG
            toast.view = toastView
            toast.show()
        }
    }


    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.apply {
            val changeLayoutParams = layoutParams as CoordinatorLayout.LayoutParams
            changeLayoutParams.gravity = Gravity.END or Gravity.TOP
            changeLayoutParams.marginEnd = resources.getDimension(R.dimen.marginBig).toInt()
            changeLayoutParams.bottomMargin = resources.getDimension(R.dimen.marginBig).toInt()
            this.layoutParams = changeLayoutParams
        }
        if (message.isNotBlank()) snackbar.show()
    }

    private fun showProgress(visible: Boolean) {
        val fragment = supportFragmentManager.findFragmentByTag(DIALOG_TAG)
        if (fragment != null && !visible) {
            (fragment as ProgressDialogFragment).dismissAllowingStateLoss()
            supportFragmentManager.executePendingTransactions()
        } else if (fragment == null && visible) {
            val progressDialogFragment = ProgressDialogFragment()
            progressDialogFragment.show(supportFragmentManager, DIALOG_TAG)
            supportFragmentManager.executePendingTransactions()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        currentFocus?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigationHolder.setNavigator(navigator)
    }

    override fun onResume() {
        super.onResume()
        if (restoringState) restoreIntent.accept(Unit)
        restoringState = false
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        restoringState = true
        super.onPause()
    }

    override fun onBackPressed() {
        val fragment = mainNavHostFragment.childFragmentManager.fragments
            .firstOrNull()
                as? BackPressedHandler
        if (fragment?.onBackPressed() != true) {
            mainNavHostFragment.childFragmentManager.popBackStackImmediate()
        }
    }
}
