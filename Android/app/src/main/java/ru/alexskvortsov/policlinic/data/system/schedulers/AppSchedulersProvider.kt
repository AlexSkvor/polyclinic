package ru.alexskvortsov.policlinic.data.system.schedulers

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AppSchedulersProvider : Scheduler {

    override fun ui(): io.reactivex.Scheduler = AndroidSchedulers.mainThread()
    override fun computation(): io.reactivex.Scheduler = Schedulers.computation()
    override fun trampoline(): io.reactivex.Scheduler = Schedulers.trampoline()
    override fun newThread(): io.reactivex.Scheduler = Schedulers.newThread()
    override fun io(): io.reactivex.Scheduler = Schedulers.io()

}