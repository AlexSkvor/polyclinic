package ru.alexskvortsov.policlinic

import timber.log.Timber

fun <T> T.alsoPrintDebug(message: String = "AlsoPrintDebug "): T =
    this.also { Timber.e("$message...  $this") }