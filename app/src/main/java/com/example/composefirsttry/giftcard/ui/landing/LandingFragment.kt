package com.example.composefirsttry.giftcard.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.composefirsttry.databinding.FragmentLandingBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.utils.requireActivity

class LandingFragment : Fragment() {
    private lateinit var binding: FragmentLandingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(false)

        //set listeners
        binding.image.setOnClickListener {
            val direction = LandingFragmentDirections.actionLandingFragmentToAddCardFragment()
            requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(true)
    }
}