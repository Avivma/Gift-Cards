package com.example.composefirsttry.giftcard.ui.favorites

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.FragmentFavoritesBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.logic.cards.model.CardTypeWrapper
import com.example.composefirsttry.giftcard.logic.cards.repository.CardUtils
import com.example.composefirsttry.giftcard.ui.favorites.states.FavoritesIntention
import com.example.composefirsttry.giftcard.ui.favorites.states.FavoritesState
import com.example.composefirsttry.utils.requireActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesAdapter

    @Inject
    lateinit var cardUtils: CardUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //More info: https://stackoverflow.com/questions/59826066/databindingutil-inflates-layout-as-null
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        adapter = FavoritesAdapter(emptyList(), cardUtils)
        adapter.setHasStableIds(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Important fix: removeObservers and getViewLifecyclerOwner instead of activity to prevent multiple call to onChanged from unremoved observers.
        //More info: https://blog.usejournal.com/observe-livedata-from-viewmodel-in-fragment-fd7d14f9f5fb

        adapter.intentionsListener.removeObservers(viewLifecycleOwner)
        adapter.intentionsListener.observe(viewLifecycleOwner, Observer { intention -> viewModel.action(intention) })

        viewModel.observeStateLiveData(viewLifecycleOwner, Observer { state ->
            if (state is FavoritesState.Navigation) navigate(state)
            else render(state)
        })

        viewModel.action(FavoritesIntention.Refresh)
    }

    private fun render(state: FavoritesState) {
        when(state) {
            FavoritesState.Waiting -> {
                L.i("FavoritesState.Waiting")
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerView.alpha = 0.5f
            }
            is FavoritesState.DisplayData -> {
                L.i("FavoritesState.DisplayData")
                binding.progressCircular.visibility = View.GONE
                binding.recyclerView.alpha = 1f
                adapter.setStores(state.stores)
            }
            is FavoritesState.StoreDialogOpened -> {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.store_dialog_title)
                    .setMessage(resources.getString(R.string.store_dialog_remove_message, state.store.storeName))
                    .setNeutralButton(R.string.store_dialog_remove_button_text) { _, _ -> viewModel.action(FavoritesIntention.RemoveStore(state.store)) }
                    .setNegativeButton(R.string.store_dialog_cancel_button_text) { dialog, _ -> dialog.dismiss() }
                    .show()
            }
            is FavoritesState.RemoveAllDialogOpened -> {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.store_dialog_title)
                    .setMessage(R.string.store_dialog_remove_all_message)
                    .setNeutralButton(R.string.remove_all_favorites) { _, _ -> viewModel.action(FavoritesIntention.RemoveAllStores) }
                    .setNegativeButton(R.string.store_dialog_cancel_button_text) { dialog, _ -> dialog.dismiss() }
                    .show()

            }
        }
    }

    private fun navigate(navigationIntention: FavoritesState.Navigation) {
        when (navigationIntention) {
            is FavoritesState.Navigation.NavigateToCardsScreen -> {
                val direction = FavoritesFragmentDirections.actionFavoritesFragmentToCardsFragment(CardTypeWrapper(navigationIntention.giftCardType))
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            else -> {
                L.e("Unfamiliar navigation (intention: ${navigationIntention.javaClass.simpleName})")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.removeFavoritesButton.setOnClickListener {
            viewModel.action(FavoritesIntention.OpenRemoveAllDialog)
        }
    }
}