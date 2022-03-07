package com.example.composefirsttry.preferencescreens.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceViewHolder
import com.example.composefirsttry.R
import com.example.composefirsttry.preferencescreens.PreferenceActivity
import com.example.composefirsttry.preferencescreens.miscellaneous.*
import com.example.composefirsttry.preferencescreens.widget.MyPreferenceCheckBox
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox
import java.util.ArrayList

class AlternativeMainPrefFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: PrefViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.preferenceManager.sharedPreferencesName = SHARD_PREF_NAME

        setPreferencesFromResource(R.xml.alternative_main_preferences, rootKey)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(PrefViewModel::class.java)

        val prefItems: ArrayList<ParentPrefItem> = viewModel.init()
        render(prefItems)
    }

    private fun render(prefList: ArrayList<ParentPrefItem>) {
//        L.i("AlternativeMainPrefFragment - render")
        prefList.forEach { prefItem ->
            val prefCheckBox: MyPreferenceCheckBox? = findPreference(prefItem.prefType.text)
            prefCheckBox?.setOnBindViewHolder { holder ->
                val triCheckBox: TriCheckBox = holder.findViewById(R.id.checkBox) as TriCheckBox
                triCheckBox.setState(prefItem.state)
                setClickListeners(holder, triCheckBox, prefItem)
            }
        }
    }

    private fun setClickListeners(holder: PreferenceViewHolder, triCheckBox: TriCheckBox, prefItem: ParentPrefItem) {
        triCheckBox.setOnStateChanged { state ->
            prefItem.state = state
            updateState(prefItem)
        }

        holder.findViewById(R.id.customizeButton).setOnClickListener {
            navigate(prefItem)
        }
    }

    private fun updateState(prefItem: ParentPrefItem) {
//        L.i("AlternativeMainPrefFragment - updateState")
        viewModel.updateState(prefItem)
    }

    private fun navigate(prefItem: ParentPrefItem) {
//        L.i("AlternativeMainPrefFragment - navigate")
        val navController = requireActivity<PreferenceActivity>().getNavController()
        navController.navigate(AlternativeMainPrefFragmentDirections.actionAlternativeMainPrefFragmentToSubPrefFragment(prefItem))
    }
}