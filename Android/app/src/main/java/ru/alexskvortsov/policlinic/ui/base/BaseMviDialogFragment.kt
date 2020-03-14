package ru.alexskvortsov.policlinic.ui.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.hannesdorfmann.mosby3.mvi.MviDialogFragment
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import ru.alexskvortsov.policlinic.domain.utils.AppDisposables
import ru.alexskvortsov.policlinic.domain.utils.DisposablesProvider
import ru.alexskvortsov.policlinic.objectScopeName
import ru.alexskvortsov.policlinic.toothpick.DI
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick

abstract class BaseMviDialogFragment<V : MvpView, P : MviPresenter<V, *>> : MviDialogFragment<V, P>(), DisposablesProvider by AppDisposables() {

    companion object {
        private const val STATE_SCOPE_NAME = "state_scope_name"
        const val PARENT_SCOPE_NAME = "parent_scope_name"
    }

    protected inline fun <reified T> fromScope(): T {
        return scope.getInstance(T::class.java)
    }

    private lateinit var fragmentScopeName: String

    protected lateinit var scope: Scope
        private set

    abstract val layoutRes: Int
        @LayoutRes get

    var dismissCallBack: (() -> Unit)? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutRes, container, true)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallBack?.invoke()
    }

    open fun installModules(scope: Scope) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        fragmentScopeName =
            savedInstanceState?.getString(STATE_SCOPE_NAME) ?: objectScopeName()

        val parentScopeName = arguments?.get(PARENT_SCOPE_NAME) ?: DI.APP_SCOPE
        if (Toothpick.isScopeOpen(fragmentScopeName)) {
            Timber.d("Get exist UI scope: $fragmentScopeName")
            scope = Toothpick.openScope(fragmentScopeName)
        } else {
            Timber.d("Init new UI scope: $fragmentScopeName")
            scope = Toothpick.openScopes(parentScopeName, fragmentScopeName)
            installModules(scope)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        clear()
        Timber.d("Destroy UI scope: $fragmentScopeName")
        Toothpick.closeScope(scope.name)
        super.onDestroy()

    }

    var isRender = false
        private set

    protected fun withRender(block: () -> Unit) {
        isRender = true
        block.invoke()
        isRender = false
    }
}