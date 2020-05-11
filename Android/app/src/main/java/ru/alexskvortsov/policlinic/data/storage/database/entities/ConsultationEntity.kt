package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.Converters

@Entity(
    tableName = "consultations_in_plan",
    foreignKeys = [
        ForeignKey(
            entity = DoctorEntity::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userAskedId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["doctorId"], unique = false),
        Index(value = ["userAskedId"], unique = false),
        Index(value = ["patientId"], unique = false),
        Index(value = ["date"], unique = false)
    ]
)
@TypeConverters(Converters::class)
data class ConsultationEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val userAskedId: String,
    val patientId: String,
    val date: LocalDateTime,
    val startTimePlan: LocalDateTime,
    val endTimePlan: LocalDateTime,
    val descriptionOfReason: String,
    val cancelled: Boolean
)