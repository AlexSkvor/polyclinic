package ru.alexskvortsov.policlinic.domain.repository

import io.reactivex.Completable

interface CancelConsultationRepository {
    fun cancelConsultation(consultationId: String): Completable
}