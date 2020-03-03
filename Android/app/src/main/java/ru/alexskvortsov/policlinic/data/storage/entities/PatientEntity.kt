package ru.alexskvortsov.policlinic.data.storage.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "patients",
    indices = [
        Index(value = arrayOf("passportNumber"), unique = true),
        Index(value = arrayOf("snilsNumber"), unique = true),
        Index(value = arrayOf("omsPoliceNumber"), unique = true),
        Index(value = arrayOf("surname"), unique = false),
        Index(value = arrayOf("phoneNumber"), unique = true),
        Index(value = arrayOf("login"), unique = true)
    ]
)
data class PatientEntity(
    @PrimaryKey val id: String,
    val passportNumber: String,
    val snilsNumber: String,
    val omsPoliceNumber: String,
    val name: String,
    val surname: String,
    val fathersName: String,
    val gender: Boolean,
    val berthDate: Date,
    val weight: Float,
    val height: Float,
    val login: String,
    val password: String,
    val phoneNumber: String
) {
}