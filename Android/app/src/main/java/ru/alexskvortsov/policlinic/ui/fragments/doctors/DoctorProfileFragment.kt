package ru.alexskvortsov.policlinic.ui.fragments.doctors

import android.annotation.SuppressLint
import android.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Observable.just
import kotlinx.android.synthetic.main.doctor_profile_fragment.*
import kotlinx.android.synthetic.main.item_doctor_competence_field.view.*
import ru.alexskvortsov.policlinic.*
import ru.alexskvortsov.policlinic.data.service.DoctorProfileService
import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.domain.repository.DoctorProfileRepository
import ru.alexskvortsov.policlinic.domain.states.doctor.DoctorProfileViewState
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorProfilePresenter
import ru.alexskvortsov.policlinic.presentation.doctor.DoctorProfileView
import ru.alexskvortsov.policlinic.ui.base.BaseMviFragment
import toothpick.Scope
import toothpick.config.Module
import java.util.concurrent.TimeUnit

class DoctorProfileFragment : BaseMviFragment<DoctorProfileView, DoctorProfilePresenter>(), DoctorProfileView {
    override val layoutRes: Int
        get() = R.layout.doctor_profile_fragment

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModules(object : Module() {
            init {
                bind(DoctorProfileRepository::class.java).to(DoctorProfileService::class.java)
            }
        })
    }

    override fun createPresenter(): DoctorProfilePresenter = fromScope()

    override fun initIntent(): Observable<Unit> = just(Unit)

    override fun nameChangedIntent(): Observable<String> = nameDoctorProfile.changes()

    override fun surnameChangedIntent(): Observable<String> = surnameDoctorProfile.changes()

    override fun fathersNameChangedIntent(): Observable<String> = fathersNameDoctorProfile.changes()

    override fun loginChangedIntent(): Observable<String> = loginDoctorProfile.changes()

    override fun phoneChangedIntent(): Observable<String> = phoneDoctorProfile.changes()

    private val competenceAddingRelay = PublishRelay.create<List<CompetenceEntity>>()
    override fun competencesChangeIntent(): Observable<List<CompetenceEntity>> = competenceAddingRelay.hide()

    override fun saveIntent(): Observable<Unit> = saveButtonDoctorProfile.clicks()

    override fun cancelIntent(): Observable<Unit> = cancelButtonDoctorProfile.clicks()

    override fun render(state: DoctorProfileViewState) = withRender {
        if (state.doctor.isEmpty()) return@withRender

        renderButtons(state.hasChanges, state.canSave)
        renderLogin(state)
        renderEditTextField(state.changedDoctor.surname, surnameDoctorProfile)
        renderEditTextField(state.changedDoctor.name, nameDoctorProfile)
        renderEditTextField(state.changedDoctor.fathersName, fathersNameDoctorProfile)
        renderEditTextField(state.changedDoctor.phone, phoneDoctorProfile)
        renderEditTextField(state.changedDoctor.skillLevel, skillLevelDoctorProfile)
        renderEditTextField(state.changedDoctor.berthDate, berthDateDoctorProfile)
        renderEditTextField(state.changedDoctor.workExperienceYears.toString(), workExperienceYearsDoctorProfile)
        renderCompetenceList(state.changedDoctor.competenceList)
        competenceTitle.setOnClickListener {
            dialogCompetences(state.possibleCompetences - state.changedDoctor.competenceList, state.changedDoctor.competenceList)
        }
    }

    private fun dialogCompetences(competences: List<CompetenceEntity>, currentCompetences: List<CompetenceEntity>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.chooseCompetence)
        builder.setItems(competences.map { it.name }.toTypedArray()) { dialog, pos ->
            competenceAddingRelay.accept(currentCompetences + competences[pos])
            dialog.dismiss()
        }
        builder.show()
    }

    @SuppressLint("InflateParams")
    private fun renderCompetenceList(competences: List<CompetenceEntity>) {
        competenceList.removeAllViews()
        competences.forEach {
            val v = layoutInflater.inflate(R.layout.item_doctor_competence_field, null)
            v.competenceField.text = it.name
            competenceList.addView(v)
        }
    }

    private fun renderLogin(state: DoctorProfileViewState) {
        renderEditTextField(state.changedDoctor.login, loginDoctorProfile)
        if (state.changedDoctor.login.isNotBlank() && state.loginChanged) {
            if (!state.loginUnique || state.verificationStarted) {
                loginDoctorProfile.inputLayout?.isErrorEnabled = true
                loginDoctorProfile.inputLayout?.error = getString(R.string.loginIsNotUnique)
            } else {
                loginDoctorProfile.inputLayout?.isErrorEnabled = false
                loginDoctorProfile.inputLayout?.error = null
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
        cancelButtonDoctorProfile.visible = hasChanges
        saveButtonDoctorProfile.visible = hasChanges

        if (hasChanges) {
            saveButtonDoctorProfile.isClickable = canSave
            if (canSave) saveButtonDoctorProfile.setTextColor(getColor(R.color.colorAccent))
            else saveButtonDoctorProfile.setTextColor(getColor(R.color.attributeNameColor))
        }
    }

    private fun TextInputEditText.changes() = this.textChanges()
        .skipInitialValue()
        .filter { !isRender }
        .map { it.toString() }
        .uiDebounce(300, TimeUnit.MILLISECONDS)
}