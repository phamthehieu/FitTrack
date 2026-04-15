package com.example.fittrack.data.local.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

class FitTrackTypeConverters {
    @TypeConverter
    fun localDateFromString(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun instantFromEpochMillis(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()
}

