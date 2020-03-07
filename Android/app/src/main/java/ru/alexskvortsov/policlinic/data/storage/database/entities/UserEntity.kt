package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = arrayOf("login"), unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val login: String,
    val password: String
)