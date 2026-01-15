package com.example.littlepainter.utils

import androidx.room.TypeConverter
import java.util.Date

class TypeConverter {
    @TypeConverter
    fun dateToLong(date: Date):Long{
        return date.time
    }
    @TypeConverter
    fun longToDate(time: Long):Date{
        return Date(time)
    }
}