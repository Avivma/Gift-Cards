package com.example.composefirsttry.preferencescreens.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.composefirsttry.R

class SubPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}