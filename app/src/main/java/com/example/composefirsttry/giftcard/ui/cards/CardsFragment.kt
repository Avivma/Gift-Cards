package com.example.composefirsttry.giftcard.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.databinding.CardsFragmentBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.utils.requireActivity

class CardsFragment : Fragment() {
    private val args: CardsFragmentArgs by navArgs()

    private lateinit var binding: CardsFragmentBinding
    private lateinit var adapter: CardAdapter

    private var card: GiftCard? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //More info: https://stackoverflow.com/questions/59826066/databindingutil-inflates-layout-as-null
        binding = CardsFragmentBinding.inflate(inflater, container, false)
        card = args.card

        val cards: MutableList<GiftCard> = emptyList<GiftCard>().toMutableList()
        if (card != null) {
            cards.add(card!!)
        } else {
            cards.addAll(getAllCards())
        }
        adapter = CardAdapter(cards)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.showClearAll = card != null
        return binding.root
    }

    private fun getAllCards(): List<GiftCard> = listOf(GiftCard.MAX, GiftCard.CORPORATE, GiftCard.HOT)

    override fun onStart() {
        super.onStart()
        binding.clearAll.setOnClickListener {
            adapter.setCards(getAllCards())
            binding.showClearAll = false
        }

        binding.addCard.setOnClickListener {
            val direction = CardsFragmentDirections.actionCardsFragmentToAddCardFragment()
            requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
        }
    }
}