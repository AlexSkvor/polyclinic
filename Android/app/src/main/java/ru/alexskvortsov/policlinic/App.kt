package ru.alexskvortsov.policlinic

import android.app.Application
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.alexskvortsov.policlinic.toothpick.AppModule
import ru.alexskvortsov.policlinic.toothpick.DI
import ru.alexskvortsov.policlinic.toothpick.DataBaseModule
import ru.alexskvortsov.policlinic.toothpick.DataModule
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initStetho()
        initToothpick()
        initThreetenABP()
        openScopes()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    @Suppress("SpellCheckingInspection")
    private fun initStetho() {
        @Suppress("SpellCheckingInspection")
        if (BuildConfig.DEBUG) {
            val initializerBuilder = Stetho
                .newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .build()
            Stetho.initialize(initializerBuilder)
        }
    }

    private fun initToothpick() {
        if (BuildConfig.DEBUG) {
            Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            Toothpick.setConfiguration(Configuration.forProduction().preventMultipleRootScopes())
        }
    }

    private fun initThreetenABP() {
        AndroidThreeTen.init(this)
    }

    private fun openScopes(){
        val dataScope = Toothpick.openScope(DI.DATA_SCOPE)
        dataScope.installModules(DataModule(this))
        val dataBaseScope = Toothpick.openScopes(DI.DATA_SCOPE, DI.DATA_BASE_SCOPE)
        dataBaseScope.installModules(DataBaseModule())
        val commonAppScope = Toothpick.openScopes(DI.DATA_SCOPE, DI.DATA_BASE_SCOPE, DI.APP_SCOPE)
        commonAppScope.installModules(AppModule())
    }
}