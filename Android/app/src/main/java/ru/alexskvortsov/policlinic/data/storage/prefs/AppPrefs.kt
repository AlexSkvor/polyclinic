package ru.alexskvortsov.policlinic.data.storage.prefs

import ru.alexskvortsov.policlinic.data.storage.database.entities.UserSecondaryEntity

interface AppPrefs {
    var localAuthorisation: Boolean
    var saveSignInUserId: List<String>
    var currentUser: UserSecondaryEntity

    fun logOut()
}