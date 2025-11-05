package it.spindox.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun fromListOfString(value: List<String>): String
    {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toListOfString(value: String): List<String>
    {
        return try {
            Gson().fromJson(value)
        } catch (e: Exception) {
            listOf()
        }
    }
}

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)