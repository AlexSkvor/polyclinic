package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registry_staffs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [Index(value = arrayOf("userId"), unique = true)]
)
data class RegistryStaffEntity(
    @PrimaryKey val id: String,
    val surname: String,
    val name: String,
    val fathersName: String,
    val userId: String
)