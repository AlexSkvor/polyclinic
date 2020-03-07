package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fact_consultation_connection_to_procedures",
    foreignKeys = [
        ForeignKey(
            entity = ProcedureEntity::class,
            parentColumns = ["id"],
            childColumns = ["procedureId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FactConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["factConsultationId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = arrayOf("procedureId"), unique = false),
        Index(value = arrayOf("factConsultationId"), unique = false)
    ]
)
data class FactConsultationToProceduresConnectionEntity(
    @PrimaryKey val id: String,
    val procedureId: String,
    val factConsultationId: String
)