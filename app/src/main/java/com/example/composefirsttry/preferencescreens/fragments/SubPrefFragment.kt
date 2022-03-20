package com.example.composefirsttry.preferencescreens.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.navArgs
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.example.composefirsttry.R
import com.example.composefirsttry.preferencescreens.PreferenceActivity.Companion.SEND_LOGS_ACTION
import com.example.composefirsttry.preferencescreens.miscellaneous.CategoryEnum
import com.example.composefirsttry.preferencescreens.miscellaneous.ParentCategoryEnum
import com.example.composefirsttry.preferencescreens.miscellaneous.SHARD_PREF_NAME
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox
import java.util.*
import kotlin.collections.ArrayList

class SubPrefFragment : PreferenceFragmentCompat() {
    private val args: SubPrefFragmentArgs by navArgs()

    private lateinit var parentItem: ParentPrefItem
    private lateinit var sp: SharedPreferences

    private var amountOfPreferences: Int = 0
    private var checkedPreferences: Int = 0

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.preferenceManager.sharedPreferencesName = SHARD_PREF_NAME
        sp = requireActivity().getSharedPreferences(SHARD_PREF_NAME, Context.MODE_PRIVATE)
        parentItem = args.parentItem
        val xmlPreferenceResource = when(parentItem.prefType) {
            ParentCategoryEnum.SWEETS -> {
                amountOfPreferences = CategoryEnum.getSweets().size
                checkedPreferences = getCheckedPref(CategoryEnum.getSweets())
                R.xml.sweets_preferences
            }
            ParentCategoryEnum.SALTS -> {
                amountOfPreferences = CategoryEnum.getSalts().size
                checkedPreferences = getCheckedPref(CategoryEnum.getSalts())
                R.xml.salts_preferences
            }
        }
        setPreferencesFromResource(xmlPreferenceResource, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        super.onPreferenceTreeClick(preference)
        if (preference is CheckBoxPreference) {
            checkedPreferences += if (preference.isChecked) 1 else -1

            parentItem.state = when (checkedPreferences) {
                amountOfPreferences -> TriCheckBox.CHECKED
                0 -> TriCheckBox.UNCHECKED
                else -> TriCheckBox.INDETERMINATE
            }
        } else if (preference?.key == "send_logs") {
            sendLocalBroadcast()
        }
        return true
    }

    private fun sendLocalBroadcast() {
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(SEND_LOGS_ACTION))
    }

    private fun getCheckedPref(categories: ArrayList<CategoryEnum>): Int = categories.filter { category ->
            this.preferenceManager.sharedPreferences.getBoolean(category.text, false)
        }.count()

    override fun onStop() {
        super.onStop()
        sp.edit().putInt(parentItem.prefType.text, parentItem.state).commit()
    }
}