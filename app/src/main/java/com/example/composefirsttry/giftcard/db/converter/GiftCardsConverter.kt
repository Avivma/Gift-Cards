//package com.example.giftcardsfun.db.converter
//
//import androidx.room.TypeConverter
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import java.lang.reflect.Type
//
//class GiftCardsConverter {
//    @TypeConverter
//    fun fromString(value: String?): List<String?>? {
//        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.getType()
//        return Gson().fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun fromArrayList(list: List<String?>?): String? {
//        val gson = Gson()
//        return gson.toJson(list)
//    }
//}