package ru.alexskvortsov.policlinic.domain.repository

import ru.alexskvortsov.policlinic.data.storage.database.entities.CompetenceEntity

interface CompetenceRepository {
    fun getAllCompetences(): List<CompetenceEntity>
}