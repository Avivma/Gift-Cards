package com.example.composefirsttry.preferencescreens.fragments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composefirsttry.L
import com.example.composefirsttry.preferencescreens.miscellaneous.CategoryEnum
import com.example.composefirsttry.preferencescreens.miscellaneous.ParentCategoryEnum
import com.example.composefirsttry.preferencescreens.miscellaneous.PrefEvents
import com.example.composefirsttry.preferencescreens.miscellaneous.SHARD_PREF_NAME
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox

class PrefViewModel(application: Application) : AndroidViewModel(application) {
    private val sp: SharedPreferences = application.getSharedPreferences(SHARD_PREF_NAME, Context.MODE_PRIVATE)

    fun init(): ArrayList<ParentPrefItem> =
        ParentCategoryEnum.values().toCollection(ArrayList()).map { category ->
            val state = sp.getInt(category.text, TriCheckBox.UNCHECKED)
            ParentPrefItem(category, state)
        }.toCollection(ArrayList())

    fun updateState(parentItem: ParentPrefItem) {
        val editor = sp.edit()
        if (parentItem.state == TriCheckBox.CHECKED || parentItem.state == TriCheckBox.UNCHECKED) {
            val childState: Boolean = parentItem.state == TriCheckBox.CHECKED
            val childItems: ArrayList<CategoryEnum> = when (parentItem.prefType) {
                ParentCategoryEnum.SWEETS -> CategoryEnum.getSweets()
                ParentCategoryEnum.SALTS -> CategoryEnum.getSalts()
            }
            childItems.forEach { item ->
                editor.putBoolean(item.text, childState)
            }
        }
        editor.putInt(parentItem.prefType.text, parentItem.state).commit()
    }
}