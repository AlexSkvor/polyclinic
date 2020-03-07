package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.Converters

@Entity(
    tableName = "patients",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = arrayOf("passportNumber"), unique = true),
        Index(value = arrayOf("snilsNumber"), unique = true),
        Index(value = arrayOf("omsPoliceNumber"), unique = true),
        Index(value = arrayOf("surname"), unique = false),
        Index(value = arrayOf("phoneNumber"), unique = true),
        Index(value = arrayOf("userId"), unique = true)
    ]
)
@TypeConverters(Converters::class)
data class PatientEntity(
    @PrimaryKey val id: String,
    val passportNumber: String,
    val snilsNumber: String,
    val omsPoliceNumber: String,
    val name: String,
    val surname: String,
    val fathersName: String,
    val gender: Boolean, //true -> men
    val berthDate: LocalDateTime,
    val weight: Float,
    val height: Float,
    val login: String,
    val password: String,
    val phoneNumber: String,
    val userId: String
) {
}