package dev.shorthouse.remindme.utilities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.shorthouse.remindme.data.RepeatInterval
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun epochSecondToDate(epochSecond: Long): ZonedDateTime {
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

        @TypeConverter
        @JvmStatic
        fun repeatIntervalToGson(repeatInterval: RepeatInterval?): String? {
            return Gson().toJson(repeatInterval);
        }

        @TypeConverter
        @JvmStatic
        fun gsonToRepeatInterval(json: String?): RepeatInterval? {
            val pairType = object : TypeToken<RepeatInterval>() {}.type
            return Gson().fromJson(json, pairType)
        }
    }
}
