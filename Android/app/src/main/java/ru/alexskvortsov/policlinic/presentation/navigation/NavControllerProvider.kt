package com.pakdd.presentation.system.navigation

import android.os.Bundle

interface NavControllerProvider{
    fun nextScreen(screenId: Int, bundle: Bundle? = null)
    fun back()
    fun backTo(screenId: Int?)
    fun replace(screenId: Int, bundle: Bundle? = null)
}