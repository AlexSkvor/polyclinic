package ru.alexskvortsov.policlinic.toothpick

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefsStorage
import ru.alexskvortsov.policlinic.data.system.ResourceManager
import ru.alexskvortsov.policlinic.data.system.schedulers.AppSchedulersProvider
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.presentation.activity.UnpackingNotifier
import toothpick.config.Module

class DataModule(context: Context): Module() {
    init {
        bind(Context::class.java).toInstance(context)
        bind(AppPrefs::class.java).to(AppPrefsStorage::class.java).singleton()
        bind(ResourceManager::class.java).singleton()
        bind(Scheduler::class.java).toInstance(AppSchedulersProvider())
        bind(UnpackingNotifier::class.java).toInstance(UnpackingNotifier())

        bind(Gson::class.java).toInstance(Gson())

        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.toString().orEmpty()
        bind(String::class.java).withName(DefaultFilesDir::class.java).toInstance(filesDir)
    }
}