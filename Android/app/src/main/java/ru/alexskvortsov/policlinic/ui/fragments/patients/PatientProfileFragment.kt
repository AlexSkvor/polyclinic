package ru.alexskvortsov.policlinic.ui.fragments.patients

import android.os.Bundle
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.Observable.just
import kotlinx.android.synthetic.main.patient_profile_fragment.*
import ru.alexskvortsov.policlinic.*
import ru.alexskvortsov.policlinic.data.service.PatientProfileService
import ru.alexskvortsov.policlinic.domain.repository.PatientProfileRepository
import ru.alexskvortsov.policlinic.domain.states.patient.PatientProfileViewState
import ru.alexskvortsov.policlinic.presentation.patient.PatientProfilePresenter
import ru.alexskvortsov.policlinic.presentation.patient.PatientProfileView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import toothpick.Scope
import toothpick.config.Module
import java.util.concurrent.TimeUnit

class PatientProfileFragment : BaseMviFragment<PatientProfileView, PatientProfilePresenter>(), PatientProfileView {

    override val layoutRes: Int
        get() = R.layout.patient_profile_fragment

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(PatientProfileRepository::class.java).to(PatientProfileService::class.java)
            }
        })
    }

    override fun createPresenter(): PatientProfilePresenter = fromScope()

    override fun initIntent(): Observable<Unit> = just(Unit)

    override fun nameChangedIntent(): Observable<String> = namePatientProfile.changes()

    override fun surnameChangedIntent(): Observable<String> = surnamePatientProfile.changes()

    override fun fathersNameChangedIntent(): Observable<String> = fathersNamePatientProfile.changes()

    override fun loginChangedIntent(): Observable<String> = loginPatientProfile.changes()

    override fun phoneChangedIntent(): Observable<String> = phonePatientProfile.changes()
        .map { it.filter { c -> c in '0'..'9' || c == '+' } }

    override fun passportChangedIntent(): Observable<String> = passportPatientProfile.changes()

    override fun omsChangedIntent(): Observable<String> = omsPatientProfile.changes()

    override fun weightChangedIntent(): Observable<String> = weightPatientProfile.changes()

    override fun heightChangedIntent(): Observable<String> = heightPatientProfile.changes()

    override fun saveIntent(): Observable<Unit> = saveButtonPatientProfile.clicks()

    override fun cancelIntent(): Observable<Unit> = cancelButtonPatientProfile.clicks()

    override fun render(state: PatientProfileViewState) = withRender {
        if (state.patient.isEmpty()) return@withRender

        renderButtons(state.hasChanges, state.canSave)
        renderEditTextField(state.changedPatient.surname, surnamePatientProfile)
        renderEditTextField(state.changedPatient.name, namePatientProfile)
        renderEditTextField(state.changedPatient.fathersName, fathersNamePatientProfile)
        renderLogin(state)
        renderEditTextField(state.changedPatient.phone, phonePatientProfile)
        renderEditTextField(state.changedPatient.passportNumber, passportPatientProfile)
        renderEditTextField(state.changedPatient.omsPoliceNumber, omsPatientProfile)
        renderEditTextField(state.changedPatient.weight, weightPatientProfile)
        renderEditTextField(state.changedPatient.height, heightPatientProfile)
        renderEditTextField(state.changedPatient.snilsNumber, snilsPatientProfile)
        renderEditTextField(state.changedPatient.berthDate, berthDatePatientProfile)
    }

    private fun renderEditTextField(text: String, view: TextInputEditText) {
        view.setTextIfNotEqual(text)
        view.inputLayout?.isErrorEnabled = text.isBlank()

        view.inputLayout?.error = if (text.isNotBlank()) null
        else getString(R.string.fieldMustNotBeEmpty)
    }

    private fun renderLogin(state: PatientProfileViewState) {
        renderEditTextField(state.changedPatient.login, loginPatientProfile)
        if (state.changedPatient.login.isNotBlank() && state.loginChanged) {
            if (!state.loginUnique || state.verificationStarted) {
                loginPatientProfile.inputLayout?.isErrorEnabled = true
                loginPatientProfile.inputLayout?.error = getString(R.string.loginIsNotUnique)
            } else {
                loginPatientProfile.inputLayout?.isErrorEnabled = false
                loginPatientProfile.inputLayout?.error = null
            }
        }
    }

    private fun renderButtons(hasChanges: Boolean, canSave: Boolean) {
        cancelButtonPatientProfile.visible = hasChanges
        saveButtonPatientProfile.visible = hasChanges

        if (hasChanges) {
            saveButtonPatientProfile.isClickable = canSave
            if (canSave) saveButtonPatientProfile.setTextColor(getColor(R.color.colorAccent))
            else saveButtonPatientProfile.setTextColor(getColor(R.color.attributeNameColor))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMasks()
    }

    private fun setMasks() {
        val passportSlots = UnderscoreDigitSlotsParser().parseSlots("____ ______")
        val maskPassport = MaskImpl.createTerminated(passportSlots)
        maskPassport.isForbidInputWhenFilled = true
        val passportWatcher = MaskFormatWatcher(maskPassport)
        passportWatcher.installOn(passportPatientProfile)

        val omsSlots = UnderscoreDigitSlotsParser().parseSlots("____ ____ ____ ____")
        val maskOms = MaskImpl.createTerminated(omsSlots)
        maskOms.isForbidInputWhenFilled = true
        val omsWatcher = MaskFormatWatcher(maskOms)
        omsWatcher.installOn(omsPatientProfile)

        val phoneSlots = PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (___) ___ - ____")
        val maskPhone = MaskImpl.createTerminated(phoneSlots)
        maskPhone.isForbidInputWhenFilled = true
        val phoneWatcher = MaskFormatWatcher(maskPhone)
        phoneWatcher.installOn(phonePatientProfile)
    }

    private fun TextInputEditText.changes() = this.textChanges()
        .skipInitialValue()
        .filter { !isRender }
        .map { it.toString() }
        .uiDebounce(300, TimeUnit.MILLISECONDS)
}