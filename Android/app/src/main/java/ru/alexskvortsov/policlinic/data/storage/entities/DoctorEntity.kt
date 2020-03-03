package ru.alexskvortsov.policlinic.data.storage.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "doctors",
    indices = [
        Index(value = arrayOf("login"), unique = true),
        Index(value = arrayOf("surname"), unique = false)
    ]
)
data class DoctorEntity(
    @PrimaryKey val id: String,
    val professionName: String,
    val workExperienceYears: Int,
    val educationMainDocumentRef: String,
    val surname: String,
    val name: String,
    val fathersName: String,
    val gender: Boolean,
    val berthDate: String,
    val login: String,
    val phone: String,
    val password: String
)