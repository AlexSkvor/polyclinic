package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "doctor_to_competence_connections",
    foreignKeys = [
        ForeignKey(
            entity = DoctorEntity::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CompetenceEntity::class,
            parentColumns = ["id"],
            childColumns = ["competenceId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = arrayOf("doctorId"), unique = false),
        Index(value = arrayOf("competenceId"), unique = false)
    ]
)
data class DoctorToCompetenceConnectionEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val competenceId: String
)