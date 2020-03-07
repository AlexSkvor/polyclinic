package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = FactConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["factConsultationId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["factConsultationId"], unique = false)]
)
data class AttachedDocumentEntity(
    @PrimaryKey val id: String,
    val factConsultationId: String,
    val fullDocumentRefOnDevice: String,
    val name: String,
    val description: String
)