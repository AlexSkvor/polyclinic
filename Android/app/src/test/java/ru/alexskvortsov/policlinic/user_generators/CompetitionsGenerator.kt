package ru.alexskvortsov.policlinic.user_generators

import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity
import ru.alexskvortsov.policlinic.uuid

class CompetitionsGenerator {

    companion object {
        val competitionNamesAndDescriptions = listOf(
            "Стоматолог" to "Лечение зубов, проблем с челюстью",
            "Лор - врач" to "Ухо-горло-нос",
            "Офтальмолог" to "Зрение",
            "Гинеколог" to "Женский врач",
            "Кардиолог" to "Сердечко прихватило?",
            "Невролог" to "Если глаз дёргается",
            "Эндокринолог" to "По гармональным вопросам",
            "Терапевт" to "Ну, в общем, тоже врач",
            "Хирург" to "Дастал ножъ?! Рэжъ!"
        )
    }

    fun generateCompetitions(): List<CompetenceEntity> {
        return competitionNamesAndDescriptions
            .map {
                CompetenceEntity(
                    id = uuid,
                    name = it.first,
                    description = it.second
                )
            }
    }

}