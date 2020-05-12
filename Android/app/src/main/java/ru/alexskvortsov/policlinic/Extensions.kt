package ru.alexskvortsov.policlinic

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun <T> T.alsoPrintDebug(message: String = "AlsoPrintDebug "): T =
    this.also { Timber.e("$message...  $this") }

fun Any.objectScopeName() = "${javaClass.simpleName}_${hashCode()}"

fun BottomNavigationView.setupWithNavControllerReselectionDisabled(navController: NavController) {
    this.setupWithNavController(navController)
    this.setOnNavigationItemReselectedListener { }
}

fun BottomNavigationView.setupLongText() {
    val menuView = this.getChildAt(0) as? ViewGroup ?: return
    menuView.forEach {
        it.findViewById<View>(R.id.largeLabel)?.setPadding(0, 0, 0, 0)
    }
}

var View.visible: Boolean
    set(value) {
        this.visibility = if (value) View.VISIBLE
        else View.GONE
    }
    get() = this.visibility == View.VISIBLE

fun EditText.onImeActionDoneClicks(): Observable<Unit> {
    return Observable.create { emitter ->
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                emitter.onNext(Unit)
                true
            } else {
                false
            }
        }
    }
}

fun Fragment.hideKeyboard() {
    val inputManager = requireActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (this.isKeyboardOpen())
        inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun View.hideKeyboard(dp: Float = 100f) {
    val inputManager = context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (this.isKeyboardOpen(dp))
        inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun View.isKeyboardOpen(dp: Float = 100f): Boolean {
    val visibleBounds = Rect()
    this.rootView.getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = rootView.height - visibleBounds.height()
    val marginOfError = this.context.convertDpToPx(dp)
        .roundToInt() //на эмуляторе получается 200dp, на планшете 100dp, оба отрабатывают проверку корректно, может не правильно работать с другими устройствами
    return heightDiff > marginOfError
}

fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

fun Fragment.getColor(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(requireContext(), colorId)
}

fun View.getColor(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(context, colorId)
}

fun EditText.setTextIfNotEqual(text: CharSequence) {
    if (text.toString() != this.text.toString()) {
        this.setText(text)
        if (text.isNotEmpty())
            setSelection(this.text.length)
    }
}

val TextInputEditText.inputLayout: TextInputLayout?
    get() {
        var mParent = parent
        while (mParent is View) {
            if (mParent is TextInputLayout) return mParent
            mParent = (mParent as View).parent
        }
        return null
    }

fun Fragment.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.view?.rootView?.getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = this.view?.rootView?.height?.minus(visibleBounds.height())
    val marginOfError = this.context?.convertDpToPx(100F)?.roundToInt()
    return if (heightDiff != null && marginOfError != null) {
        heightDiff > marginOfError
    } else false
}

inline fun <reified T : Any> Bundle.withArgument(key: String, value: T?): Bundle {
    return if (value == null) this
    else when (value) {
        is String -> this.also { putString(key, value) }
        is Int -> this.also { putInt(key, value) }
        is Long -> this.also { putLong(key, value) }
        is Boolean -> this.also { putBoolean(key, value) }
        else -> throw IllegalArgumentException("Wrong value typeChosen! ${value::class}")
    }
}

fun <T> Observable<T>.uiDebounce(delay: Long, timeUnits: TimeUnit = TimeUnit.MILLISECONDS): Observable<T> {
    return this.debounce(delay, timeUnits).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.uiThrottleFirst(delay: Long, timeUnits: TimeUnit = TimeUnit.MILLISECONDS): Observable<T> =
    this.throttleFirst(delay, timeUnits).observeOn(AndroidSchedulers.mainThread())

private const val patternDB = "MM.dd.yyyy HH:mm:ss"
val formatterDB: DateTimeFormatter = DateTimeFormatter.ofPattern(patternDB)

private const val patternDBDate = "MM.dd.yyyy"
val formatterDBDate: DateTimeFormatter = DateTimeFormatter.ofPattern(patternDBDate)

private const val patternUIDate = "dd.MM.yyyy"
val formatterUiDate: DateTimeFormatter = DateTimeFormatter.ofPattern(patternUIDate)

private const val patternUITime = "HH:mm"
val formatterUiTime: DateTimeFormatter = DateTimeFormatter.ofPattern(patternUITime)

private const val patternUIDateTime = "dd.MM.yyyy HH:mm"
val formatterUiDateTime: DateTimeFormatter = DateTimeFormatter.ofPattern(patternUIDateTime)