package dev.shorthouse.remindme.utilities

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromEpochSecond(epochSecond: Long): ZonedDateTime {
            return ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(epochSecond),
                ZoneId.systemDefault()
            )
        }

        @TypeConverter
        @JvmStatic
        fun dateToEpochSecond(dateTime: ZonedDateTime): Long {
            return dateTime.toEpochSecond()
        }


    }
}
