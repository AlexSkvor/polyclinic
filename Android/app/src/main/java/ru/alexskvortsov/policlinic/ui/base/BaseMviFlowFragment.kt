package ru.alexskvortsov.policlinic.ui.base

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import ru.alexskvortsov.policlinic.presentation.navigation.NavigationController
import ru.alexskvortsov.policlinic.presentation.navigation.Navigator
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject

abstract class BaseMviFlowFragment<V: MvpView, P: MviPresenter<V, *>>: BaseMviFragment<V, P>() {

    abstract val navHostId: Int

    val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), navHostId)
    }

    private val navigationController: NavigationController by lazy {
        NavigationController(navController)
    }

    private val navigator get() = Navigator(navigationController)

    @Inject
    lateinit var navigationHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    override fun installModules(scope: Scope) {
        scope.installModules(object : Module() {
            init {
                val cicerone = Cicerone.create()
                bind(ru.terrakok.cicerone.Router::class.java).toInstance(cicerone.router)
                bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toothpick.inject(this, scope)
    }

    override fun onResume() {
        super.onResume()
        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed(): Boolean {
        val childFragment = childFragmentManager.findFragmentById(navHostId)
            ?.childFragmentManager
            ?.fragments
            ?.firstOrNull()

        return if (childFragment is BackPressedHandler) childFragment.onBackPressed()
        else super.onBackPressed()
    }
}