package ru.alexskvortsov.policlinic.data.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserSecondaryEntity
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo
import javax.inject.Inject

class AppPrefsStorage @Inject constructor(private val context: Context) : AppPrefs {

    companion object {
        private const val LAST_USERS_SIGN_IN_LOCAL = "sign in last users"
        private const val AUTHORISATION_LOCAL = "authorisation local"

        private const val USER_ID_PREFS = "USER_ID_PREFS"
        private const val REAL_USER_ID_PREFS = "REAL_USER_ID_PREFS"
        private const val USER_NAME_PREFS = "USER_NAME_PREFS"
        private const val USER_INITIAL_SURNAME_LETTER_PREFS = "USER_INITIAL_SURNAME_LETTER_PREFS"
        private const val USER_TYPE_PREFS = "USER_TYPE_PREFS"
    }

    private val sharedPrefs: SharedPreferences
            by lazy { context.getSharedPreferences("appPrefs", 0) }

    override var localAuthorisation: Boolean
        get() = sharedPrefs.getBoolean(AUTHORISATION_LOCAL, false)
        set(value) {
            sharedPrefs.edit {
                putBoolean(AUTHORISATION_LOCAL, value)
            }
        }

    override var saveSignInUserId: List<String>
        get() {
            val innerStr = sharedPrefs.getString(LAST_USERS_SIGN_IN_LOCAL, "") ?: ""
            return innerStr.split('|').filterNot { it.isEmpty() }.take(2)
        }
        set(value) {
            sharedPrefs.edit {
                var innerStr = ""
                value.take(2).forEach { innerStr += "$it|" }
                putString(LAST_USERS_SIGN_IN_LOCAL, innerStr)
            }
        }
    override var currentUser: UserSecondaryEntity
        get() {
            return object : UserSecondaryEntity {
                override val userId: String
                    get() = sharedPrefs.getString(USER_ID_PREFS, "") ?: ""

                override val fullName: String
                    get() = sharedPrefs.getString(USER_NAME_PREFS, "") ?: ""

                override val initialSurnameLetter: String
                    get() = sharedPrefs.getString(USER_INITIAL_SURNAME_LETTER_PREFS, "") ?: ""

                override val realId: String
                    get() = sharedPrefs.getString(REAL_USER_ID_PREFS, "") ?: ""

                override val type: UserAuthInfo.UserType
                    get() = when (sharedPrefs.getInt(USER_TYPE_PREFS, -1)) {
                        0 -> UserAuthInfo.UserType.DOCTOR
                        1 -> UserAuthInfo.UserType.REGISTRY
                        2 -> UserAuthInfo.UserType.PATIENT
                        else -> throw IllegalArgumentException("user was not saved before!")
                    }
            }
        }
        set(value) {
            sharedPrefs.edit{
                putString(USER_ID_PREFS, value.userId)
                putString(USER_NAME_PREFS, value.fullName)
                putString(USER_INITIAL_SURNAME_LETTER_PREFS, value.initialSurnameLetter)
                putString(REAL_USER_ID_PREFS, value.realId)
                putInt(USER_TYPE_PREFS, value.type.code)
            }
        }

    override fun logOut() {
        //TODO("Not yet implemented")
    }

}