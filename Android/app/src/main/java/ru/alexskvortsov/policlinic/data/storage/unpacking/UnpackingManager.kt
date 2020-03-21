package ru.alexskvortsov.policlinic.data.storage.unpacking

import io.reactivex.Completable
import ru.alexskvortsov.policlinic.data.storage.database.dao.UserDao
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import javax.inject.Inject

class UnpackingManager @Inject constructor(
    private val userDao: UserDao,
    private val scheduler: Scheduler,
    private val usersUnpacker: UsersUnpacker
) {

    fun unpackIfNeeded(): Completable = userDao.count()
        .flatMapCompletable {
            if (it > 0) Completable.complete()
            else usersUnpacker.unpack()
        }.subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())
}