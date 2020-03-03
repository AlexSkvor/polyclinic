package ru.alexskvortsov.policlinic.data.system

import android.content.Context
import javax.inject.Inject

class ResourceManager @Inject constructor(private val context: Context) {
    fun getString(resId: Int): String = context.getString(resId)
    fun getString(resId: Int, vararg formatArgs: Any?): String = context.getString(resId, *formatArgs)
}