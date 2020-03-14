package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import ru.alexskvortsov.policlinic.domain.defaultIfNull
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo
import java.util.*

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
    override val userId: String
) : UserSecondaryEntity {

    override val fullName: String
        get() = "$surname ${name.toUpperCase(Locale.ROOT).firstOrNull().defaultIfNull("")}. " +
                "${fathersName.toUpperCase(Locale.ROOT).firstOrNull().defaultIfNull("")}."

    override val initialSurnameLetter: String
        get() = surname.toUpperCase(Locale.ROOT).firstOrNull().defaultIfNull("").toString()

    override val type: UserAuthInfo.UserType
        get() = UserAuthInfo.UserType.REGISTRY

    override val realId: String
        get() = id
}