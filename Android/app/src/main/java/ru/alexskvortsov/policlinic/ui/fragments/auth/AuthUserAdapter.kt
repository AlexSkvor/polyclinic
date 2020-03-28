package ru.alexskvortsov.policlinic.ui.fragments.auth

import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.include_change_local_password.view.*
import kotlinx.android.synthetic.main.include_local_sign_in.view.*
import kotlinx.android.synthetic.main.item_authorization_user_holder.view.*
import ru.alexskvortsov.policlinic.R
import ru.alexskvortsov.policlinic.domain.states.auth.*
import ru.alexskvortsov.policlinic.domain.subscribeBy
import ru.alexskvortsov.policlinic.hideKeyboard
import ru.alexskvortsov.policlinic.onImeActionDoneClicks
import ru.alexskvortsov.policlinic.ui.utils.delegate.DelegateAdapter
import ru.alexskvortsov.policlinic.ui.utils.delegate.UserAction
import ru.alexskvortsov.policlinic.visible


private const val INVALIDATE_GROUP_CHANGE_VIEW = -1


class AuthUserAdapter(
    private val compositeDisposable: CompositeDisposable,
    private val onClick: (Int) -> Unit
) : DelegateAdapter<UserAuthInfo>() {

    private var canClick: Boolean = true
    private var selectPositionItem: Int = INVALIDATE_GROUP_CHANGE_VIEW
    private var actionHandlings = PublishRelay.create<AuthElementInfo>()

    private val tryLogin = PublishRelay.create<UserAction.TryLogin>()
    private val tryChangeUserPassword = PublishRelay.create<UserAction.TryChangePassword>()

    private val cancelEditPassword = PublishRelay.create<UserAction.Cancel>()

    override fun getAction(): Observable<UserAction<UserAuthInfo>> {
        //view
        val cancel = cancelEditPassword.hide()

        //save button actions
        val login = tryLogin.hide()
        val changePas = tryChangeUserPassword.hide()

        val actionsList = listOf(cancel, login, changePas)
        return Observable.merge(actionsList)
    }

    override fun onBind(item: UserAuthInfo, holder: DelegateViewHolder) =
        with(holder.itemView) {
            val visible = selectPositionItem == holder.adapterPosition
            TransitionManager.beginDelayedTransition(userItemContainer, Slide(Gravity.BOTTOM))
            initViewContent(visible, item)
            initNavigationButton(item, holder)
            actionSet(item)
            setupChangeHandlings(holder)
        }

    private fun View.setupChangeHandlings(holder: DelegateViewHolder) {
        actionHandlings
            .subscribe {
                if (selectPositionItem != holder.adapterPosition)
                    resetAll()
                else {
                    when (it.fieldType) {
                        FieldType.LOGIN_PASSWORD -> invalidatePassword(it.passwordState, localPasswordInputLayout)
                        FieldType.OLD_PASSWORD -> invalidatePassword(it.passwordState, changeInputCurrentPasswordInputLayout)
                        FieldType.CHANGE_PASSWORD -> invalidatePassword(it.passwordState, inputNewPasswordInputLayout)
                        FieldType.REPEAT_CHANGE_PASSWORD -> invalidatePassword(it.passwordState, repeatInputNewPasswordInputLayout)
                    }
                }
            }.bind()

        localPasswordEditText.adapterTextChanges {
            setDefaultState(listOf(localPasswordInputLayout))
        }.bind()

        changeInputCurrentPasswordEditText
            .adapterTextChanges {
                setDefaultState(listOf(changeInputCurrentPasswordInputLayout, inputNewPasswordInputLayout, repeatInputNewPasswordInputLayout))
            }.bind()

        inputNewPasswordEditText
            .adapterTextChanges {
                setDefaultState(listOf(inputNewPasswordInputLayout, repeatInputNewPasswordInputLayout))
            }.bind()

        repeatInputNewPasswordEditText
            .adapterTextChanges {
                setDefaultState(listOf(repeatInputNewPasswordInputLayout))
            }.bind()
    }

    private fun View.setDefaultState(layouts: List<TextInputLayout?>) =
        layouts.forEach {
            invalidatePassword(PasswordState.VALID, it)
        }

    @CheckResult
    private fun TextView.adapterTextChanges(onEdit: (message: String) -> Unit) =
        RxTextView.textChanges(this)
            .map { it.toString() }
            .distinctUntilChanged()
            .skip(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy({ message -> onEdit(message) })

    private fun View.initViewContent(visible: Boolean, user: UserAuthInfo) {
        fullNameView.text = user.fullName
        initialSurnameView.text = user.initialSurnameLetter

        require(user.password.isNotEmpty())

        localSignInGroup.visible = visible
        groupChangePassword.visible = false
        changeInputCurrentPasswordInputLayout.visible = false
    }

    private fun View.initNavigationButton(user: UserAuthInfo, holder: DelegateViewHolder) {
        initialSurnameView.setOnClickListener { itemClicked(this, holder) }
        fullNameView.setOnClickListener { itemClicked(this, holder) }
        cancel(user)
        changePassword(user)
    }

    private fun View.cancel(user: UserAuthInfo) {
        cancelNewPasswordView.setOnClickListener {
            resetAll()
            if (user.password.isEmpty()) {
                groupChangePassword.visible = false
            } else {
                cancelEditPassword.accept(UserAction.Cancel)
                groupChangePassword.visible = !groupChangePassword.visible
                localSignInGroup.visible = !localSignInGroup.visible
                changeInputCurrentPasswordInputLayout.visible = false
            }
        }
    }

    private fun View.changePassword(user: UserAuthInfo) {
        changeLocalPassword.setOnClickListener {
            groupChangePassword.visible = !groupChangePassword.visible
            localSignInGroup.visible = !localSignInGroup.visible
            changeInputCurrentPasswordInputLayout.visible = user.password.isNotBlank() && !changeInputCurrentPasswordInputLayout.visible
            resetAll()
        }
    }

    private fun View.actionSet(user: UserAuthInfo) {
        saveNewPasswordView.setOnClickListener {
            passwordAction(user)
        }
        repeatInputNewPasswordEditText.onImeActionDoneClicks().subscribe {
            passwordAction(user)
        }.bind()


        localSignIn.setOnClickListener {
            loginAction(user)
        }
        localPasswordEditText.onImeActionDoneClicks().subscribe {
            loginAction(user)
        }.bind()
    }

    private fun View.passwordAction(user: UserAuthInfo) {
        hideKeyboard()
        changeOrCreatePassword(user)
    }


    private fun View.loginAction(user: UserAuthInfo) {
        hideKeyboard()
        tryLogin.accept(UserAction.TryLogin(InputUserPassword(user.id, localPasswordEditText.text.toString())))
    }

    private fun View.changeOrCreatePassword(user: UserAuthInfo) {
        val changePasswordUser = ChangePasswordUser(
            user.id, changeInputCurrentPasswordEditText.text.toString(),
            inputNewPasswordEditText.text.toString(), repeatInputNewPasswordEditText.text.toString()
        )

        tryChangeUserPassword.accept(UserAction.TryChangePassword(changePasswordUser))
    }

    fun processState(state: AuthElementInfo) = actionHandlings.accept(state)

    private fun View.invalidatePassword(passwordValidation: PasswordState, currentTextInputLayout: TextInputLayout?) {
        when (passwordValidation) {
            PasswordState.VALID -> {
                currentTextInputLayout?.error = null
                currentTextInputLayout?.isErrorEnabled = false
            }
            PasswordState.INVALID -> {
                currentTextInputLayout?.isErrorEnabled = true
                currentTextInputLayout?.error = this.context.getString(R.string.invalid_password)
            }
            PasswordState.EMPTY -> {
                currentTextInputLayout?.isErrorEnabled = true
                currentTextInputLayout?.error = this.context.getString(R.string.require_password)
            }
            PasswordState.NOT_EQUAL -> {
                currentTextInputLayout?.isErrorEnabled = true
                currentTextInputLayout?.error = this.context.getString(R.string.password_invalidate_duplicate)
            }
        }

    }

    private fun resetPasswordData(passwordEditText: EditText? = null, passwordInputLayout: TextInputLayout? = null) {
        passwordInputLayout?.endIconMode = TextInputLayout.END_ICON_NONE
        passwordInputLayout?.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        passwordInputLayout?.setEndIconActivated(false)
        passwordEditText?.setText("")
        passwordInputLayout?.error = null
        passwordInputLayout?.isErrorEnabled = false
    }

    private fun itemClicked(view: View, holder: DelegateViewHolder) {
        val transition = Slide(Gravity.TOP)
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                canClick = true
            }

            override fun onTransitionResume(transition: Transition) = Unit
            override fun onTransitionPause(transition: Transition) = Unit
            override fun onTransitionCancel(transition: Transition) = Unit
            override fun onTransitionStart(transition: Transition) {
                onClick.invoke(holder.adapterPosition)
            }
        })
        if (canClick) {
            val visibleChangePassword = !view.localSignInGroup.visible && view.groupChangePassword.visible
            val visibleLocalSingIn = view.localSignInGroup.visible && !view.groupChangePassword.visible

            if (visibleChangePassword) {
                resetPasswordData(view.changeInputCurrentPasswordEditText, view.changeInputCurrentPasswordInputLayout)
                resetPasswordData(view.inputNewPasswordEditText, view.inputNewPasswordInputLayout)
                resetPasswordData(view.repeatInputNewPasswordEditText, view.repeatInputNewPasswordInputLayout)
            }
            canClick = false
            TransitionManager.beginDelayedTransition(view.userItemContainer, transition)
            notifyItemChanged(selectPositionItem)
            selectPositionItem = if (visibleLocalSingIn || visibleChangePassword) INVALIDATE_GROUP_CHANGE_VIEW else holder.adapterPosition
            notifyItemChanged(holder.adapterPosition)
            view.localSignInGroup.visible = !view.localSignInGroup.visible
            view.resetAll()
        }
        view.saveNewPasswordView.visible = false
    }

    private fun Disposable.bind() {
        compositeDisposable.add(this)
    }

    private fun View.resetAll() {
        listOf(changeInputCurrentPasswordEditText, inputNewPasswordEditText, repeatInputNewPasswordEditText, localPasswordEditText).forEach {
            it?.setText("")
        }
        listOf(
            changeInputCurrentPasswordInputLayout,
            inputNewPasswordInputLayout,
            repeatInputNewPasswordInputLayout,
            localPasswordInputLayout
        ).forEach {
            it?.isErrorEnabled = false
            it?.error = null
        }
    }

    override fun getLayoutId(): Int = R.layout.item_authorization_user_holder

    override fun isForViewType(items: List<*>, position: Int): Boolean =
        items[position] is UserAuthInfo
}