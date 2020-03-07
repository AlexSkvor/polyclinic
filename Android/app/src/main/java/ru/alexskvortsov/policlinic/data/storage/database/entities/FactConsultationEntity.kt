package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.Converters

@Entity(
    tableName = "fact_consultations",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultationId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = arrayOf("consultationId"), unique = false)
    ]
)
@TypeConverters(Converters::class)
data class FactConsultationEntity(
    @PrimaryKey val id: String,
    val consultationId: String,
    val startTimeFact: LocalDateTime,
    val endTimeFact: LocalDateTime,
    val notes: String
)