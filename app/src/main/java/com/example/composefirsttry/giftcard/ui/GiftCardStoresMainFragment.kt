package com.example.composefirsttry.giftcard.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.L
import com.example.composefirsttry.MyApplication
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.FragmentGiftCardStoresMainBinding
import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.giftcard.ui.states.StoreMainState
import com.example.composefirsttry.giftcard.ui.states.StoresMainIntention
import com.example.composefirsttry.giftcard.viewmodel.StoresMainViewModel
import com.example.composefirsttry.giftcard.viewmodel.StoresMainViewModelFactory
import com.example.composefirsttry.preferencescreens.miscellaneous.getApplication
import javax.inject.Inject

class GiftCardStoresMainFragment : Fragment() {
    private lateinit var viewModel: StoresMainViewModel
    private lateinit var binding: FragmentGiftCardStoresMainBinding
    private lateinit var adapter: StoresAdapter

    @Inject
    lateinit var sp: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getApplication<MyApplication>().component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_card_stores_main, container, false)!!

        viewModel = ViewModelProvider(this, StoresMainViewModelFactory(requireActivity().application, viewLifecycleOwner))
            .get(StoresMainViewModel::class.java)

        adapter = StoresAdapter(viewModel.getStores())

        binding.storeRecyclerView.adapter = adapter
        binding.storeRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        L.i("Checkboxes state BEFORE attach model: binding.maxCheckBox= ${binding.maxCheckBox.isChecked}, binding.corporateCheckBox= ${binding.corporateCheckBox.isChecked}, binding.hotCheckBox= ${binding.hotCheckBox.isChecked}")
        binding.model = viewModel
        L.i("Checkboxes state AFTER attach model: binding.maxCheckBox= ${binding.maxCheckBox.isChecked}, binding.corporateCheckBox= ${binding.corporateCheckBox.isChecked}, binding.hotCheckBox= ${binding.hotCheckBox.isChecked}")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Important fix: removeObservers and getViewLifecyclerOwner instead of activity to prevent multiple call to onChanged from unremoved observers.
        //More info: https://blog.usejournal.com/observe-livedata-from-viewmodel-in-fragment-fd7d14f9f5fb
        viewModel.stateLiveData.removeObservers(viewLifecycleOwner)
        viewModel.stateLiveData.observe(viewLifecycleOwner, Observer { state -> render(state) })
    }

    private fun render(state: StoreMainState) {
        when(state) {
            StoreMainState.Waiting -> {
                binding.progressCircular.visibility = if (state.progressBarVisible) View.VISIBLE else View.GONE
                binding.storeRecyclerView.alpha = if (state.storesListFaded) 0.5f else 1f
            }
            is StoreMainState.DisplayData -> {
                binding.progressCircular.visibility = if (state.progressBarVisible) View.VISIBLE else View.GONE
                binding.storeRecyclerView.alpha = if (state.storesListFaded) 0.5f else 1f
                adapter.setStores(state.stores)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.maxCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.action(StoresMainIntention.FilterByCard(GiftCard.MAX, isChecked))
        }
        binding.corporateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.action(StoresMainIntention.FilterByCard(GiftCard.CORPORATE, isChecked))
        }
        binding.hotCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.action(StoresMainIntention.FilterByCard(GiftCard.HOT, isChecked))
        }

        binding.searchButton.setOnClickListener {
            viewModel.action(StoresMainIntention.FilterByPrefix(binding.searchStore.text.toString()))
        }
    }

//    fun refresh() {
//        viewModel.action(StoresMainIntention.Refresh)
//    }
}
