package ru.alexskvortsov.policlinic.ui.fragments.auth

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.jakewharton.rxbinding3.material.selections
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_authorization.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.data.storage.service.AuthorizationService
import ru.alexskvortsov.policlinic.domain.repository.AuthorizationRepository
import ru.alexskvortsov.policlinic.domain.states.auth.*
import ru.alexskvortsov.policlinic.domain.utils.RecyclerItem
import ru.alexskvortsov.policlinic.presentation.auth.AuthorizationPresenter
import ru.alexskvortsov.policlinic.presentation.auth.AuthorizationView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment
import ru.alexskvortsov.policlinic.ui.utils.delegate.CompositeDelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.UserAction
import ru.alexskvortsov.policlinic.uiDebounce
import ru.alexskvortsov.policlinic.visible
import toothpick.Scope
import toothpick.config.Module
import java.util.concurrent.TimeUnit

class AuthFragment : BaseMviFragment<AuthorizationView, AuthorizationPresenter>(), AuthorizationView {

    override fun createPresenter(): AuthorizationPresenter = fromScope()

    private lateinit var newUserAdapter: AuthUserAdapter
    private lateinit var compositeAdapter: CompositeDelegateAdapter<RecyclerItem>

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(PasswordValidation::class.java).toInstance(PasswordValidation())
                bind(AuthorizationRepository::class.java).to(AuthorizationService::class.java)
            }
        })
    }

    override val layoutRes: Int
        get() = R.layout.fragment_authorization

    override fun cancelEditPassword(): Observable<Unit> =
        compositeAdapter.actions
            .ofType(UserAction.Cancel::class.java)
            .map { Unit }

    override fun tryLogin(): Observable<InputUserPassword> =
        compositeAdapter.actions
            .ofType(UserAction.TryLogin::class.java)
            .map { it.password }

    override fun tryChangePasswordUser(): Observable<ChangePasswordUser> =
        compositeAdapter.actions
            .ofType(UserAction.TryChangePassword::class.java)
            .map { it.password }

    override fun getUserList(): Observable<Unit> = Observable.just(Unit)

    override fun userType(): Observable<UserAuthInfo.UserType> =
        userTypeTabs.selections()
            .map {
                when (it.position) {
                    0 -> UserAuthInfo.UserType.DOCTOR
                    1 -> UserAuthInfo.UserType.REGISTRY
                    else -> UserAuthInfo.UserType.PATIENT
                }
            }.startWith(UserAuthInfo.UserType.PATIENT)

    override fun searchUser(): Observable<String> = RxSearchView.queryTextChanges(userSearchView)
        .map { it.toString() }
        .uiDebounce(300, TimeUnit.MILLISECONDS)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())

        newUserAdapter = AuthUserAdapter(compositeDisposable) { position ->
            if (position !in layoutManager.findFirstCompletelyVisibleItemPosition()..layoutManager.findLastCompletelyVisibleItemPosition())
                usersRecyclerView.scrollToPosition(position)
        }
        compositeAdapter = CompositeDelegateAdapter.Companion.Builder<RecyclerItem>()
            .add(newUserAdapter)
            .add(AuthTitleAdapter())
            .build()

        logOutView.setOnClickListener { requireActivity().finish() }

        usersRecyclerView.adapter = compositeAdapter
        usersRecyclerView.layoutManager = layoutManager
    }

    override fun render(viewState: AuthorizationViewState) {
        updateUsersList(viewState)
        setError(viewState)
    }

    private fun updateUsersList(state: AuthorizationViewState) {
        if (state.updateListUsers) {
            compositeAdapter.replaceData(state.userList.withRecentAndTitles())
            listEmptyHolderContainer.visible = !state.emptyListVisible
            usersRecyclerView.visible = state.emptyListVisible
        }
    }

    private fun setError(state: AuthorizationViewState) {
        state.authInfo?.let {
            newUserAdapter.processState(it)
        }
    }

    private fun List<UserAuthInfo>.withRecentAndTitles(): List<RecyclerItem> {
        val recentTitle = AuthTitleItem(getString(R.string.last))
        val allTitle = AuthTitleItem(getString(R.string.all))
        val recentList = this.filter { it.userUseToApp != NOT_IN_HISTORY }
            .sortedBy { it.userUseToApp }
            .take(2)

        return if (recentList.isEmpty()) listOf(allTitle) + this
        else listOf(recentTitle) + recentList + listOf(allTitle) + this
    }

}