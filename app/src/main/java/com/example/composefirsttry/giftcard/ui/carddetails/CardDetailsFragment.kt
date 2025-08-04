package com.example.composefirsttry.giftcard.ui.carddetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.composefirsttry.L
import com.example.composefirsttry.databinding.FragmentCardDetailsBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.ui.addcard.AddCardFragment
import com.example.composefirsttry.giftcard.ui.carddetails.state.CardDetailsIntention
import com.example.composefirsttry.giftcard.ui.carddetails.state.CardDetailsState
import com.example.composefirsttry.giftcard.ui.utils.NavigateOutsideHandler
import com.example.composefirsttry.giftcard.ui.utils.UiUtils
import com.example.composefirsttry.utils.requireActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CardDetailsFragment : Fragment() {
    private val viewModel: CardDetailsViewModel by viewModels()
    private val args: CardDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentCardDetailsBinding

    @Inject
    lateinit var navigateOutsideHandler: NavigateOutsideHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //More info: https://stackoverflow.com/questions/59826066/databindingutil-inflates-layout-as-null
        binding = FragmentCardDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Important fix: removeObservers and getViewLifecyclerOwner instead of activity to prevent multiple call to onChanged from unremoved observers.
        //More info: https://blog.usejournal.com/observe-livedata-from-viewmodel-in-fragment-fd7d14f9f5fb

        viewModel.observeStateLiveData(viewLifecycleOwner, Observer { state ->
            if (state is CardDetailsState.Navigation) navigate(state)
            else render(state)
        })

        viewModel.action(CardDetailsIntention.Refresh(args.card))
    }

    private fun navigate(navigationIntention: CardDetailsState.Navigation) {
        when (navigationIntention) {
            CardDetailsState.Navigation.NavigateToLandingScreen -> {
                val direction = CardDetailsFragmentDirections.actionCardDetailsFragmentToLandingFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            CardDetailsState.Navigation.NavigateBackToCards -> {
                val direction = CardDetailsFragmentDirections.actionCardDetailsFragmentToCardsFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is CardDetailsState.Navigation.NavigateToEditCard -> {
                val direction = CardDetailsFragmentDirections.actionCardDetailsFragmentToAddCardFragment(navigationIntention.card, AddCardFragment.CARD_DETAILS_SCREEN)
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is CardDetailsState.Navigation.NavigateOutsideToApplication -> navigateOutsideHandler.launchApplication(navigationIntention.applicationId)
            is CardDetailsState.Navigation.NavigateOutsideToWebsite -> navigateOutsideHandler.launchSite(navigationIntention.siteAddress)
        }
    }

    private fun render(state: CardDetailsState) {
        when (state) {
            CardDetailsState.RemoveCardDialogOpened -> removeCardDialogOpened()
            is CardDetailsState.DisplayData -> {
                binding.card = state.cardDetailsExtended
            }
            else -> throw Exception("Unfamiliar CardDetailsState (${state.javaClass.simpleName})")
        }
    }

    private fun removeCardDialogOpened() {
        UiUtils.getRemoveCardDialog(requireActivity()) {
            viewModel.action(CardDetailsIntention.RemoveCard) }
            .show()
    }

    override fun onStart() {
        super.onStart()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(false)
        binding.editButton.setOnClickListener {
            viewModel.action(CardDetailsIntention.NavigateToEditCard)
        }
        binding.removeButton.setOnClickListener {
            viewModel.action(CardDetailsIntention.OpenRemoveCardDialog)
        }
        binding.loadMoneyButton.setOnClickListener {
            viewModel.action(CardDetailsIntention.NavigateOutsideToLoadMoney)
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(true)
    }
}