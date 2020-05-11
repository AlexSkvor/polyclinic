package ru.alexskvortsov.policlinic.data.storage.database.entities

import androidx.room.*
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.data.storage.database.Converters
import ru.alexskvortsov.policlinic.domain.defaultIfNull
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo
import ru.alexskvortsov.policlinic.domain.utils.SpinnerItem
import java.util.*

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
    val skillLevel: String,
    val workExperienceYears: Int,
    val educationMainDocumentRef: String,
    val surname: String,
    val name: String,
    val fathersName: String,
    val gender: Boolean, //true -> men
    val berthDate: LocalDateTime,
    val phone: String,
    override val userId: String
): UserSecondaryEntity, SpinnerItem {

    override val fullName: String
        get() = "$surname ${name.toUpperCase(Locale.ROOT).firstOrNull().defaultIfNull("")}. " +
                "${fathersName.toUpperCase(Locale.ROOT).firstOrNull().defaultIfNull("")}."

    override val initialSurnameLetter: String
        get() = surname.toUpperCase(Locale.ROOT).firstOrNull().defaultIfNull("").toString()

    override val type: UserAuthInfo.UserType
        get() = UserAuthInfo.UserType.DOCTOR

    override val realId: String
        get() = id

    override val uuid: String
        get() = id

    override val nameSpinner: String
        get() = fullName
}