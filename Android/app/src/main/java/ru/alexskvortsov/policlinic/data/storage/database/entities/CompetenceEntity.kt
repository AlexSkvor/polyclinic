package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "competencies")
data class CompetenceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val minutesForConsultation: Int
)