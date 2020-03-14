package ru.alexskvortsov.policlinic.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import ru.alexskvortsov.policlinic.BuildConfig
import ru.alexskvortsov.policlinic.domain.utils.AppDisposables
import ru.alexskvortsov.policlinic.domain.utils.DisposablesProvider
import ru.alexskvortsov.policlinic.objectScopeName
import ru.alexskvortsov.policlinic.toothpick.DI
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick

private const val STATE_SCOPE_NAME = "state_scope_name"

abstract class BaseMviFragment<V : MvpView, P : MviPresenter<V, *>> : MviFragment<V, P>(),
    DisposablesProvider by AppDisposables(), BackPressedHandler {

    abstract val layoutRes: Int
        @LayoutRes get

    private var externalParentScopeName: String = ""

    private lateinit var fragmentScopeName: String

    protected lateinit var scope: Scope
        private set

    protected open fun installModules(scope: Scope) {}

    private val mLifeCycleLogsEnabled = BuildConfig.DEBUG

    protected inline fun <reified T> fromScope(): T = scope.getInstance(T::class.java)

    private var instanceStateSaved: Boolean = false

    private val parentScopeName: String by lazy {
        if (externalParentScopeName.isEmpty()) (parentFragment?.parentFragment as? BaseMviFragment<*, *>)
            ?.fragmentScopeName ?: DI.APP_SCOPE
        else externalParentScopeName
    }

    fun setParentScopeOwner(parent: Any) {
        externalParentScopeName = parent.objectScopeName()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fragmentScopeName = savedInstanceState?.getString(STATE_SCOPE_NAME) ?: objectScopeName()
        if (Toothpick.isScopeOpen(fragmentScopeName)) {
            Timber.d("Get exist UI scope: $fragmentScopeName")
            scope = Toothpick.openScope(fragmentScopeName)
        } else {
            Timber.d("Init new UI scope: $fragmentScopeName")
            scope = Toothpick.openScopes(parentScopeName, fragmentScopeName)
            installModules(scope)
        }
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            log("onCreate(): fragment re-created from savedInstanceState")
        } else {
            log("onCreate(): fragment created anew")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        instanceStateSaved = true
        outState.putString(STATE_SCOPE_NAME, fragmentScopeName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    private fun needCloseScope(): Boolean =
        when {
            activity?.isChangingConfigurations == true -> false
            activity?.isFinishing == true -> true
            else -> isRealRemoving()
        }

    private fun isRealRemoving(): Boolean =
        (isRemoving && !instanceStateSaved) ||
                ((parentFragment as? BaseMviFragment<*, *>)?.isRealRemoving() ?: false)


    override fun onDestroy() {
        super.onDestroy()
        if (needCloseScope()) {
            Timber.d("Destroy UI scope: $fragmentScopeName")
            Toothpick.closeScope(scope.name)
        }
        clear()
        log("onDestroy()")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated()")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        log("onViewStateRestored()")
    }

    override fun onStart() {
        super.onStart()
        log("onStart()")
    }

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
        log("onResume()")
    }

    override fun onPause() {
        super.onPause()
        log("onPause()")
    }

    override fun onStop() {
        super.onStop()
        log("onStop()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log("onDestroyView()")
    }

    private fun log(log: String) {
        if (mLifeCycleLogsEnabled) {
            Timber.d("${javaClass.simpleName} - $log")
        }
    }

    var isRender = false
        private set

    override fun onBackPressed(): Boolean = false
    protected fun withRender(block: () -> Unit) {
        isRender = true
        block.invoke()
        isRender = false
    }
}