package ru.alexskvortsov.policlinic.presentation.navigation

class Screens {
    object AuthScreen : BundleScreen() {
        override val screenId: Int
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    }

    object PatientScreen : BundleScreen() {
        override val screenId: Int
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    }

    object DoctorScreen : BundleScreen() {
        override val screenId: Int
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    }

    object RegistarScreen : BundleScreen() {
        override val screenId: Int
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    }

    companion object {
        val startScreensIds = listOf(AuthScreen.screenId, PatientScreen.screenId, DoctorScreen.screenId, RegistarScreen.screenId)
    }
}