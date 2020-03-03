package ru.alexskvortsov.policlinic.data.storage.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

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
data class TimeSheetEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val date: Date,
    val startTime: Date, //min 6:00 max 22:00
    val endTime: Date //min startTime + 1:00 max 23:00
)