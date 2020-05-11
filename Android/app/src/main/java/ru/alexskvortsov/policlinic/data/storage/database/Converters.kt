package ru.alexskvortsov.policlinic.data.storage.database

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime
import ru.alexskvortsov.policlinic.formatterDB

class Converters {

    @TypeConverter
    fun localDateTimeToString(time: LocalDateTime): String = time.format(formatterDB)

    @TypeConverter
    fun stringToLocalDateTime(time: String): LocalDateTime = LocalDateTime.parse(time, formatterDB)

}