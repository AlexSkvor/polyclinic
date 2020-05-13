package ru.alexskvortsov.policlinic.domain.states.records.record_info

import ru.alexskvortsov.policlinic.domain.repository.CancelConsultationRepository
import ru.alexskvortsov.policlinic.domain.repository.FactConsultationRepository
import javax.inject.Inject

class ConsultationInfoInteractor @Inject constructor(
    private val createRepository: FactConsultationRepository,
    private val cancelRepository: CancelConsultationRepository
) {
}