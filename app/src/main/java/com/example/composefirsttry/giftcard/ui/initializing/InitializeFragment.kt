package com.example.composefirsttry.giftcard.ui.initializing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.composefirsttry.L
import com.example.composefirsttry.databinding.InitializeFragmentBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.ui.initializing.state.InitializeIntention
import com.example.composefirsttry.giftcard.ui.initializing.state.InitializeState
import com.example.composefirsttry.utils.requireActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InitializeFragment : Fragment() {
    private val viewModel: InitializeViewModel by viewModels()
    private lateinit var binding: InitializeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = InitializeFragmentBinding.inflate(inflater, container, false)
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
        viewModel.observeStateLiveData(viewLifecycleOwner, Observer { state ->
            if (state is InitializeState.Navigation) navigate(state)
            else L.e("Unfamiliar InitializeState (state: ${state.javaClass.simpleName})")
        })
        viewModel.action(InitializeIntention.Initializing)
    }

    private fun navigate(navigationIntention: InitializeState.Navigation) {
        when (navigationIntention) {
            is InitializeState.Navigation.NavigateToMainScreen -> {
                val direction = InitializeFragmentDirections.actionInitializeFragmentToGiftCardsMainFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is InitializeState.Navigation.NavigateToLandingScreen -> {
                val direction = InitializeFragmentDirections.actionInitializeFragmentToLandingFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            else -> {
                L.e("Unfamiliar navigation (intention: ${navigationIntention.javaClass.simpleName})")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(false)
    }

    override fun onStop() {
        super.onStop()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(true)
    }
}