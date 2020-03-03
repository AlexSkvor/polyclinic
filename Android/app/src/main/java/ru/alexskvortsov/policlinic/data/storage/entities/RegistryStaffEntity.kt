package ru.alexskvortsov.policlinic.data.storage.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registry_staffs",
    indices = [Index(value = arrayOf("login"), unique = true)]
)
data class RegistryStaffEntity(
    @PrimaryKey val id: String,
    val login: String,
    val password: String,
    val surname: String,
    val name: String,
    val fathersName: String
)