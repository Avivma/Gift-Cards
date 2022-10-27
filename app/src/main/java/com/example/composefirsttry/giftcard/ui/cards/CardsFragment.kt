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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.i("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        L.i("onCreateView")
        //More info: https://stackoverflow.com/questions/59826066/databindingutil-inflates-layout-as-null
        binding = CardsFragmentBinding.inflate(inflater, container, false)
        adapter = CardAdapter(emptyList())
        adapter.setHasStableIds(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        sendArgsToViewModel()
        return binding.root
    }

    private fun sendArgsToViewModel() {
        val args = arguments?.get("card")
        if (args != null) {
            viewModel.setArgCard(args as GiftCard)
            arguments?.clear()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        adapter.intentionsListener.removeObservers(viewLifecycleOwner)
        if (!adapter.intentionsListener.hasObservers()) {
            L.i("adapter.intentionsListener.hasObservers() = false")
            adapter.intentionsListener.observe(viewLifecycleOwner, { intention ->
                if (intention is CardsIntention.Navigation) navigate(intention)
                else viewModel.action(intention)
            })
        }

//        viewModel.stateLiveData.removeObservers(viewLifecycleOwner)
        if (!viewModel.stateLiveData.hasObservers()) {
            L.i("viewModel.stateLiveData.hasObservers() = false")
            viewModel.stateLiveData.observe(viewLifecycleOwner, { state -> render(state) })
        }

//        viewModel.navigationLiveData.removeObservers(viewLifecycleOwner)
        if (!viewModel.navigationLiveData.hasObservers()) {
            L.i("viewModel.navigationLiveData.hasObservers() = false")
            viewModel.navigationLiveData.observe(viewLifecycleOwner, { state -> navigate(state) })
        }

        viewModel.action(CardsIntention.Refresh)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        L.i("onDestroyView")
//        adapter.intentionsListener.removeObservers(viewLifecycleOwner)
//        viewModel.stateLiveData.removeObservers(viewLifecycleOwner)
//        viewModel.navigationLiveData.removeObservers(viewLifecycleOwner)
//    }

    override fun onDestroy() {
        super.onDestroy()
        L.i("onDestroy")
//        adapter.intentionsListener.removeObservers(viewLifecycleOwner)
//        viewModel.stateLiveData.removeObservers(viewLifecycleOwner)
//        viewModel.navigationLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun navigate(navigationIntention: CardsIntention.Navigation) {
        when (navigationIntention) {
            is CardsIntention.Navigation.NavigateToEditCard -> {
                val direction = CardsFragmentDirections.actionCardsFragmentToAddCardFragment(navigationIntention.card)
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is CardsIntention.Navigation.NavigateToAddCard -> {
                val direction = CardsFragmentDirections.actionCardsFragmentToAddCardFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is CardsIntention.Navigation.NavigateToLandingScreen -> {
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
        L.i("openRemoveCardDialog - remove card dialog display (touch = $dialogDisplayFromTouch)")
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.cards_remove_card_dialog_title)
            .setNeutralButton(R.string.cards_dialog_remove_button_text) { _, _ -> viewModel.action(CardsIntention.RemoveCard(card)) }
            .setNegativeButton(R.string.cards_dialog_cancel_button_text) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener {
                dialogDisplayFromTouch = false
                it.dismiss()
            }
            .show()
    }

    override fun onStart() {
        super.onStart()
        binding.clearAll.setOnClickListener {
            viewModel.action(CardsIntention.ClearAll)
        }

        binding.addCard.setOnClickListener {
            navigate(CardsIntention.Navigation.NavigateToAddCard)
        }

        adapter.dialogIntentionCallback = { card ->
            dialogDisplayFromTouch = true
            viewModel.action(CardsIntention.OpenRemoveCardDialog(card))
        }
    }

    override fun onStop() {
        super.onStop()
        adapter.dialogIntentionCallback = null
    }

    companion object {
        var dialogDisplayFromTouch = false
    }
}