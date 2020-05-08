package ru.alexskvortsov.policlinic.presentation.navigation

import ru.alexskvortsov.policlinic.R

class Screens {
    object AuthScreen : BundleScreen() {
        override val screenId: Int
            get() = R.id.authorizationFragment
    }

    object PatientScreen : BundleScreen() {
        override val screenId: Int
            get() = R.id.patientsFragment
    }

    object DoctorScreen : BundleScreen() {
        override val screenId: Int
            get() = R.id.doctorsFragment
    }

    object RegistryScreen : BundleScreen() {
        override val screenId: Int
            get() = R.id.registryFragment
    }

    companion object {
        val startScreensIds = listOf(AuthScreen.screenId, PatientScreen.screenId, DoctorScreen.screenId, RegistryScreen.screenId)
    }
}