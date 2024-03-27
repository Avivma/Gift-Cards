package com.example.composefirsttry.giftcard.logic.stores.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ShoppingClubConverters {
    @TypeConverter
    fun fromString(value: String): List<Boolean> {
        val listType: Type = object : TypeToken<List<Boolean>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<Boolean>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}