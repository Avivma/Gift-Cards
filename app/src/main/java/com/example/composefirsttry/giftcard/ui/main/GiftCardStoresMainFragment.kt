package com.example.composefirsttry.giftcard.ui.main

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.FragmentGiftCardStoresMainBinding
import com.example.composefirsttry.databinding.GiftCardWithFrameLayoutBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.cards.repository.CardUtils
import com.example.composefirsttry.giftcard.ui.main.states.StoreMainState
import com.example.composefirsttry.giftcard.ui.main.states.StoresMainIntention
import com.example.composefirsttry.utils.bindChecked
import com.example.composefirsttry.utils.requireActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GiftCardStoresMainFragment : Fragment() {
    private val viewModel: StoresMainViewModel by viewModels()
    private lateinit var binding: FragmentGiftCardStoresMainBinding
    private lateinit var adapter: StoresAdapter

    @Inject
    lateinit var sp: SharedPreferences

    @Inject
    lateinit var cardUtils: CardUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_card_stores_main, container, false)!!


    /*  //just for reminder
        viewModel = ViewModelProvider(this, StoresMainViewModelFactory(viewLifecycleOwner))
            .get(StoresMainViewModel::class.java)*/

        adapter = StoresAdapter(emptyList(), requireContext(), sp, cardUtils)
        adapter.setHasStableIds(true)
        binding.storeRecyclerView.adapter = adapter
        binding.storeRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        L.i("Checkboxes state BEFORE attach model: binding.maxCheckBox= ${binding.maxCheckBox.checkBoxCross.visibility == View.VISIBLE}, binding.corporateCheckBox= ${binding.corporateCheckBox.checkBoxCross.visibility == View.VISIBLE}, binding.hotCheckBox= ${binding.hotCheckBox.checkBoxCross.visibility == View.VISIBLE}")
        binding.model = viewModel
        L.i("Checkboxes state AFTER attach model: binding.maxCheckBox= ${binding.maxCheckBox.checkBoxCross.visibility == View.VISIBLE}, binding.corporateCheckBox= ${binding.corporateCheckBox.checkBoxCross.visibility == View.VISIBLE}, binding.hotCheckBox= ${binding.hotCheckBox.checkBoxCross.visibility == View.VISIBLE}")

        handleOnBackPressed()
        return binding.root
    }

    private fun handleOnBackPressed() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Important fix: removeObservers and getViewLifecyclerOwner instead of activity to prevent multiple call to onChanged from unremoved observers.
        //More info: https://blog.usejournal.com/observe-livedata-from-viewmodel-in-fragment-fd7d14f9f5fb

        adapter.intentionsListener.removeObservers(viewLifecycleOwner)
        adapter.intentionsListener.observe(viewLifecycleOwner, Observer { intention ->
            if (intention is StoresMainIntention.Navigation) navigate(intention)
            else viewModel.action(intention)
        })

        viewModel.stateLiveData.removeObservers(viewLifecycleOwner)
        viewModel.stateLiveData.observe(viewLifecycleOwner, Observer { state -> render(state) })

        viewModel.action(StoresMainIntention.Refresh)
    }

    private fun render(state: StoreMainState) {
        when(state) {
            StoreMainState.Waiting -> {
                L.i("StoreMainState.Waiting")
                binding.progressCircular.visibility = if (state.progressBarVisible) View.VISIBLE else View.GONE
                binding.storeRecyclerView.alpha = if (state.storesListFaded) 0.5f else 1f
            }
            is StoreMainState.DisplayData -> {
                L.i("StoreMainState.DisplayData")
                binding.progressCircular.visibility = if (state.progressBarVisible) View.VISIBLE else View.GONE
                binding.storeRecyclerView.alpha = if (state.storesListFaded) 0.5f else 1f
                if (state.hideStoreSelectionFilter) {
                    binding.storesSelection.visibility = View.GONE
                    binding.storesClearSelection.visibility = View.GONE
                }
                adapter.setStores(state.stores)
                binding.maxCardVisible = state.cardModel.hasCard(GiftCardType.MAX)
                binding.corporateCardVisible = state.cardModel.hasCard(GiftCardType.ISRACARD)
                binding.hotCardVisible= state.cardModel.hasCard(GiftCardType.TAV_HAHAM)
                binding.maxCheckBox.cardNameLayoutWithFrame = state.cardModel.getName(GiftCardType.MAX)
                binding.corporateCheckBox.cardNameLayoutWithFrame = state.cardModel.getName(GiftCardType.ISRACARD)
                binding.hotCheckBox.cardNameLayoutWithFrame = state.cardModel.getName(GiftCardType.TAV_HAHAM)
            }
            is StoreMainState.StoreSelected -> {
                L.i("StoreMainState.StoreSelected")
                binding.storesSelection.visibility = if (state.storeSelectionFilterVisible) View.VISIBLE else View.GONE
                binding.storesClearSelection.visibility = if (state.storeSelectionFilterVisible) View.VISIBLE else View.GONE
                adapter.storeSelected(state.store)
            }
            is StoreMainState.StoreDialogOpened -> {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.store_dialog_title)
                    .setMessage(resources.getString(R.string.store_dialog_add_message, state.store.storeName))
                    .setNeutralButton(R.string.store_dialog_add_button_text) { _, _ -> viewModel.action(StoresMainIntention.AddStoreToFavorites(state.store)) }
                    .setNegativeButton(R.string.store_dialog_cancel_button_text) { dialog, _ -> dialog.dismiss() }
                    .show()

            }
        }
    }

    private fun navigate(navigationIntention: StoresMainIntention.Navigation) {
        when (navigationIntention) {
            is StoresMainIntention.Navigation.NavigateToCardsScreen -> {
                val direction = GiftCardStoresMainFragmentDirections.actionGiftCardsMainFragmentToCardsFragment(navigationIntention.card)
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            else -> {
                L.e("Unfamiliar navigation (intention: ${navigationIntention.javaClass.simpleName})")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        setCheckBoxListener(binding.maxCheckBox, GiftCard.MAX)
        setCheckBoxListener(binding.corporateCheckBox, GiftCard.CORPORATE)
        setCheckBoxListener(binding.hotCheckBox, GiftCard.HOT)
        binding.storesSelection.setOnClickListener { viewModel.action(StoresMainIntention.FilterBySelectedStores) }
        binding.storesClearSelection.setOnClickListener { viewModel.action(StoresMainIntention.ClearStoresSelection) }
        binding.separationSearchMarkButton.setOnClickListener { //I use this way (not MVI) because, there is a problem with the livedata 2-way databinding. Updating this "searchTextMutableLiveData" doesn't reflect on the UI
            binding.searchStore.setText("${binding.searchStore.text} || ")
            binding.searchStore.setSelection(binding.searchStore.length())
        }
    }

    private fun setCheckBoxListener(checkBoxLayout: GiftCardWithFrameLayoutBinding, giftCard: GiftCard) {
        checkBoxLayout.checkBox.setOnClickListener { view ->
            if (view is CheckedTextView) {
                view.toggle()
                bindChecked(checkBoxLayout.checkBox, view.isChecked)
                bindChecked(checkBoxLayout.checkBoxCross, view.isChecked)
                viewModel.action(StoresMainIntention.FilterByCard(giftCard, view.isChecked))
            }
        }

        checkBoxLayout.checkBox.setOnLongClickListener {
            Toast.makeText(requireContext(), cardToastMessage(giftCard), Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun cardToastMessage(giftCard: GiftCard) =
        "${giftCard.name} card has ${giftCard.discount.toString().removeSuffix(".0")}% discount"

//    fun refresh() {
//        viewModel.action(StoresMainIntention.Refresh)
//    }
}
