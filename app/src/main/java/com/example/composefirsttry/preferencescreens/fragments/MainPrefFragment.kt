package com.example.composefirsttry.preferencescreens.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.MainPreferenceFragmentBinding
import com.example.composefirsttry.preferencescreens.PreferenceActivity
import com.example.composefirsttry.preferencescreens.miscellaneous.PrefEvents
import com.example.composefirsttry.preferencescreens.miscellaneous.requireActivity
import java.util.ArrayList

class MainPrefFragment : Fragment() {
    private lateinit var binding: MainPreferenceFragmentBinding
    private lateinit var viewModel: PrefViewModel
    private lateinit var adapter: PrefAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_preference_fragment, container, false)

        adapter = PrefAdapter(arrayListOf())

        binding.preferenceRV.adapter = adapter
        binding.preferenceRV.layoutManager = LinearLayoutManager(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application))
            .get(PrefViewModel::class.java)
        initObservers()
        viewModel.init()
    }


    private fun initObservers() {
//        L.i("MainPrefFragment - initObservers")
        val observer = observer()
        adapter.intentionsLiveData.observe(this.viewLifecycleOwner, observer)
        viewModel.intentionsLiveData?.observe(this.viewLifecycleOwner, observer)
    }

    private fun observer() = Observer<PrefEvents> { event ->
//        L.i("MainPrefFragment - observer")
        when (event) {
            is PrefEvents.LandingScreen -> render(event.prefList)
            is PrefEvents.StateChanged -> updateState(event.prefItem)
            is PrefEvents.Customize -> navigate(event.prefItem)
        }
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun removeObservers() {
//        L.i("MainPrefFragment - removeObservers")
        adapter.intentionsLiveData.removeObservers(this.viewLifecycleOwner)
        viewModel.resetObserver(this.viewLifecycleOwner)
    }


    private fun render(prefList: ArrayList<ParentPrefItem>) {
//        L.i("MainPrefFragment - render")
        adapter.setData(prefList)
    }

    private fun updateState(prefItem: ParentPrefItem) {
//        L.i("MainPrefFragment - updateState")
        viewModel.updateState(prefItem)
    }

    private fun navigate(prefItem: ParentPrefItem) {
//        L.i("MainPrefFragment - navigate")
        val navController = requireActivity<PreferenceActivity>().getNavController()
        navController.navigate(MainPrefFragmentDirections.actionMainPreferenceFragmentToSubPreferenceFragment(prefItem))
    }
}