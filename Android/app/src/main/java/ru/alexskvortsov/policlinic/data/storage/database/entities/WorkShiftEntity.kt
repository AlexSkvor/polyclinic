package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.Converters

@Entity(
    tableName = "time_sheet",
    foreignKeys = [
        ForeignKey(
            entity = DoctorEntity::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [Index(value = arrayOf("doctorId"), unique = false)]
)
@TypeConverters(Converters::class)
data class WorkShiftEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val startTime: LocalDateTime, //min 6:00 max 22:00
    val endTime: LocalDateTime //min startTime + 1:00 max 23:00
)