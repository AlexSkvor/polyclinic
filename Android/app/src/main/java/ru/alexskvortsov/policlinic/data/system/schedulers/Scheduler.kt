package ru.alexskvortsov.policlinic.data.system.schedulers

import io.reactivex.Scheduler

interface Scheduler {
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun trampoline(): Scheduler
    fun newThread(): Scheduler
    fun io(): Scheduler
}