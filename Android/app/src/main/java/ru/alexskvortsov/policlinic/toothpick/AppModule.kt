package ru.alexskvortsov.policlinic.toothpick

import ru.alexskvortsov.policlinic.data.system.SystemMessage
import toothpick.config.Module

class AppModule: Module() {
    init {
        bind(SystemMessage::class.java).singleton()

    }
}