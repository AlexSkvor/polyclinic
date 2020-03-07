package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.Converters

@Entity(
    tableName = "consultations_in_plan",
    foreignKeys = [
        ForeignKey(
            entity = WorkShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["workShiftId"],
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
    indices = [Index(value = ["workShiftId"], unique = false),
        Index(value = ["userAskedId"], unique = false),
        Index(value = ["patientId"], unique = false)]
)
@TypeConverters(Converters::class)
data class ConsultationEntity(
    @PrimaryKey val id: String,
    val workShiftId: String,
    val userAskedId: String,
    val patientId: String,
    val startTimePlan: LocalDateTime,
    val endTimePlan: LocalDateTime,
    val descriptionOfReason: String,
    val cancelled: Boolean
)