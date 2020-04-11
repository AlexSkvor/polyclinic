package ru.alexskvortsov.policlinic.ui.fragments.registry

import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.Observable.just
import kotlinx.android.synthetic.main.registry_profile_fragment.*
import ru.alexskvortsov.policlinic.*
import ru.alexskvortsov.policlinic.data.service.RegistryProfileService
import ru.alexskvortsov.policlinic.domain.repository.RegistryProfileRepository
import ru.alexskvortsov.policlinic.domain.states.registry.profile.RegistryProfileViewState
import ru.alexskvortsov.policlinic.presentation.registry.RegistryProfilePresenter
import ru.alexskvortsov.policlinic.presentation.registry.RegistryProfileView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment
import toothpick.Scope
import toothpick.config.Module
import java.util.concurrent.TimeUnit

class RegistryProfileFragment : BaseMviFragment<RegistryProfileView, RegistryProfilePresenter>(), RegistryProfileView {

    override val layoutRes: Int
        get() = R.layout.registry_profile_fragment

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(RegistryProfileRepository::class.java).to(RegistryProfileService::class.java)
            }
        })
    }

    override fun createPresenter(): RegistryProfilePresenter = fromScope()

    override fun initIntent(): Observable<Unit> = just(Unit)

    override fun nameChangedIntent(): Observable<String> = nameRegistryProfile.changes()
    override fun surnameChangedIntent(): Observable<String> = surnameRegistryProfile.changes()
    override fun fathersNameChangedIntent(): Observable<String> = fathersNameRegistryProfile.changes()
    override fun loginChangedIntent(): Observable<String> = loginRegistryProfile.changes()

    override fun saveIntent(): Observable<Unit> = saveButtonRegistryProfile.clicks()
    override fun cancelIntent(): Observable<Unit> = cancelButtonRegistryProfile.clicks()

    override fun render(state: RegistryProfileViewState) = withRender {
        if (state.person.isEmpty()) return@withRender
        renderButtons(state.hasChanges, state.canSave)

        renderEditTextField(state.changedPerson.surname, surnameRegistryProfile)
        renderEditTextField(state.changedPerson.name, nameRegistryProfile)
        renderEditTextField(state.changedPerson.fathersName, fathersNameRegistryProfile)
        renderLogin(state)
    }

    private fun renderLogin(state: RegistryProfileViewState) {
        renderEditTextField(state.changedPerson.login, loginRegistryProfile)
        if (state.changedPerson.login.isNotBlank() && state.loginChanged) {
            if (!state.loginUnique || state.verificationStarted) {
                loginRegistryProfile.inputLayout?.isErrorEnabled = true
                loginRegistryProfile.inputLayout?.error = getString(R.string.loginIsNotUnique)
            } else {
                loginRegistryProfile.inputLayout?.isErrorEnabled = false
                loginRegistryProfile.inputLayout?.error = null
            }
        }
    }

    private fun renderEditTextField(text: String, view: TextInputEditText) {
        view.setTextIfNotEqual(text)
        view.inputLayout?.isErrorEnabled = text.isBlank()

        view.inputLayout?.error = if (text.isNotBlank()) null
        else getString(R.string.fieldMustNotBeEmpty)
    }

    private fun renderButtons(hasChanges: Boolean, canSave: Boolean) {
        cancelButtonRegistryProfile.visible = hasChanges
        saveButtonRegistryProfile.visible = hasChanges

        if (hasChanges) {
            saveButtonRegistryProfile.isClickable = canSave
            if (canSave) saveButtonRegistryProfile.setTextColor(getColor(R.color.colorAccent))
            else saveButtonRegistryProfile.setTextColor(getColor(R.color.attributeNameColor))
        }
    }

    private fun TextInputEditText.changes() = this.textChanges()
        .skipInitialValue()
        .filter { !isRender }
        .map { it.toString() }
        .uiDebounce(300, TimeUnit.MILLISECONDS)
}