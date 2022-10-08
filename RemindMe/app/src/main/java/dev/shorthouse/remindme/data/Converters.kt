package dev.shorthouse.remindme.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.ZonedDateTime

@Suppress("UtilityClassWithPublicConstructor")
class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun startDateTimeToString(startDateTime: ZonedDateTime): String {
            return startDateTime.toString()
        }

        @TypeConverter
        @JvmStatic
        fun startDateTimeFromString(startDateTimeString: String): ZonedDateTime {
            return ZonedDateTime.parse(startDateTimeString)
        }

        @TypeConverter
        @JvmStatic
        fun repeatIntervalToJson(repeatInterval: RepeatInterval?): String? {
            return Gson().toJson(repeatInterval)
        }

        @TypeConverter
        @JvmStatic
        fun repeatIntervalFromJson(json: String?): RepeatInterval? {
            return Gson().fromJson(json, object : TypeToken<RepeatInterval?>() {}.type)
        }
    }
}
