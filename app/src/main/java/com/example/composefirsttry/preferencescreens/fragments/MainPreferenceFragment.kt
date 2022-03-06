package com.example.composefirsttry.preferencescreens.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.composefirsttry.R

class MainPreferenceFragment : Fragment() {

    companion object {
        fun newInstance() = MainPreferenceFragment()
    }

    private lateinit var viewModel: MainPreferenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_preference_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainPreferenceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}