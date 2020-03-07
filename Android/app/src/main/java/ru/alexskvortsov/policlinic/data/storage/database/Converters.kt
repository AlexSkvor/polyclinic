package ru.alexskvortsov.policlinic.data.storage.database

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Converters {

    private val pattern = "MM.dd.yyyy HH:mm:ss"
    private val formatter = DateTimeFormatter.ofPattern(pattern)

    @TypeConverter
    fun localDateTimeToString(time: LocalDateTime): String = time.format(formatter)

    @TypeConverter
    fun stringToLocalDateTime(time: String): LocalDateTime = LocalDateTime.parse(time, formatter)

}