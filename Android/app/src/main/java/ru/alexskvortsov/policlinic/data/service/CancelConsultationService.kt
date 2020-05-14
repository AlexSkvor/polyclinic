package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Completable
import ru.alexskvortsov.policlinic.data.storage.database.dao.ConsultationDao
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.repository.CancelConsultationRepository
import javax.inject.Inject

class CancelConsultationService @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val scheduler: Scheduler
) : CancelConsultationRepository {
    override fun cancelConsultation(consultationId: String): Completable = consultationDao.marksCancelled(consultationId)
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.ui())
}