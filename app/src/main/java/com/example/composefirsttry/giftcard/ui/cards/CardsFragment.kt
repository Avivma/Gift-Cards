package com.example.composefirsttry.giftcard.ui.cards

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.CardsFragmentBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.logic.cards.model.CardTypeWrapper
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.ui.cards.states.CardsIntention
import com.example.composefirsttry.giftcard.ui.cards.states.CardsState
import com.example.composefirsttry.utils.requireActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardsFragment : Fragment() {
    private val viewModel: CardsViewModel by viewModels()

    private lateinit var binding: CardsFragmentBinding
    private lateinit var adapter: CardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //More info: https://stackoverflow.com/questions/59826066/databindingutil-inflates-layout-as-null
        binding = CardsFragmentBinding.inflate(inflater, container, false)
        adapter = CardAdapter(emptyList())
//        adapter.setHasStableIds(true) //add animation, BUT slow down cards entrance
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        sendArgsToViewModel()
        return binding.root
    }

    private fun sendArgsToViewModel() {
        val args = arguments?.get("cardType")
        if (args != null) {
            viewModel.setArgCardType((args as CardTypeWrapper).giftCardType)
            arguments?.clear()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.intentionsListener.observe(viewLifecycleOwner, { intention -> viewModel.action(intention) })
/*
        //try1
        viewModel.setObserver(true)
        viewModel.stateLiveData.observe(viewLifecycleOwner, { state -> render(state) })
        viewModel.setObserver(false)
        viewModel.navigationLiveData.observe(viewLifecycleOwner, { state -> navigate(state) })
        //try2
        stateLiveDataObserver = viewModel.stateLiveData.observeFreshly(viewLifecycleOwner, { state -> render(state) })
        navigationLiveDataObserver = viewModel.navigationLiveData.observeFreshly(viewLifecycleOwner, { state -> navigate(state) })
*/
        viewModel.observeStateLiveData(viewLifecycleOwner, { state ->
            if (state is CardsState.Navigation) navigate(state)
            else render(state)
        })

        viewModel.action(CardsIntention.Refresh)
    }

    private fun navigate(navigationIntention: CardsState.Navigation) {
        when (navigationIntention) {
            is CardsState.Navigation.NavigateToEditCard -> {
                val direction = CardsFragmentDirections.actionCardsFragmentToAddCardFragment(navigationIntention.card)
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is CardsState.Navigation.NavigateToAddCard -> {
                val direction = CardsFragmentDirections.actionCardsFragmentToAddCardFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is CardsState.Navigation.NavigateToLandingScreen -> {
                val direction = CardsFragmentDirections.actionCardsFragmentToLandingFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            else -> {
                L.e("Unfamiliar navigation (intention: ${navigationIntention.javaClass.simpleName})")
            }
        }
    }

    private fun render(state: CardsState) {
        when (state) {
            is CardsState.DisplayData -> displayData(state.giftCards, state.showClearAll)
            is CardsState.RemoveCardDialogOpened -> openRemoveCardDialog(state.giftCard)
            else -> L.e("Unfamiliar CardsIntention (${state.javaClass.simpleName})")
        }
    }

    private fun displayData(giftCards: List<GiftCard>, showClearAll: Boolean) {
        binding.showClearAll = showClearAll
        adapter.setCards(giftCards)
    }

    private fun openRemoveCardDialog(card: GiftCard) {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.cards_remove_card_dialog_title)
            .setNeutralButton(R.string.cards_dialog_remove_button_text) { _, _ -> viewModel.action(CardsIntention.RemoveCard(card)) }
            .setNegativeButton(R.string.cards_dialog_cancel_button_text) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onStart() {
        super.onStart()
        binding.clearAll.setOnClickListener {
            viewModel.action(CardsIntention.ClearAll)
        }

        binding.addCard.setOnClickListener {
            navigate(CardsState.Navigation.NavigateToAddCard)
        }
    }
}