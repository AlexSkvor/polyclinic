package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.Converters

@Entity(
    tableName = "doctors",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = arrayOf("surname"), unique = false),
        Index(value = arrayOf("userId"), unique = true)
    ]
)
@TypeConverters(Converters::class)
data class DoctorEntity(
    @PrimaryKey val id: String,
    val professionName: String,
    val workExperienceYears: Int,
    val educationMainDocumentRef: String,
    val surname: String,
    val name: String,
    val fathersName: String,
    val gender: Boolean, //true -> men
    val berthDate: LocalDateTime,
    val phone: String,
    val userId: String
)