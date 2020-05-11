package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.alexskvortsov.policlinic.domain.utils.SpinnerItem

@Entity(tableName = "competencies")
data class CompetenceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String
): SpinnerItem{
    override val uuid: String
        get() = id

    override val nameSpinner: String
        get() = name
}