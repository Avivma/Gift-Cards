package com.example.composefirsttry.giftcard.ui.addcard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.AddCardFragmentBinding
import com.example.composefirsttry.databinding.CardDialogRowBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.utils.requireActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddCardFragment : Fragment() {
    private val viewModel: AddCardViewModel by viewModels()
    private lateinit var binding: AddCardFragmentBinding

    // TODO: 13/10/2022 use encrypted sp (https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
    @Inject
    lateinit var sp: SharedPreferences 

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_card_fragment, container, false)!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(false)
        binding.addCardButton.setOnClickListener {
            if (checkCardDetails())
                navigateBackToCardsScreen()
        }
        binding.cardImageLayout.setOnClickListener { openCardsList() }
        binding.selectCard.setOnClickListener { openCardsList() }


        setCardDetailListener(binding.cardNameEditText, binding.cardNameLayout)
        setCardDetailListener(binding.cardDiscountEditText, binding.cardDiscountLayout)
        setCardDetailListener(binding.cardNumberEditText, binding.cardNumberLayout)
        setCardDetailListener(binding.cardCvvEditText, binding.cardCvvLayout)
        setCardDetailListener(binding.cardExpirationEditText, binding.cardExpirationLayout)
    }

    override fun onStop() {
        super.onStop()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(true)
    }

    private fun setCardDetailListener(editText: TextInputEditText, textInputLayout: TextInputLayout) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !textInputLayout.error.isNullOrEmpty()) {
                textInputLayout.error = null
            }
        }
    }

    private fun checkCardDetails(): Boolean {
        var errorFound = checkDetail(binding.cardNameEditText.text.toString(), binding.cardNameLayout, "binding.cardNameLayout")
        errorFound = checkDetail(binding.cardDiscountEditText.text.toString(), binding.cardDiscountLayout, "binding.cardDiscountLayout") || errorFound
        errorFound = checkDetail(binding.cardNumberEditText.text.toString(), binding.cardNumberLayout, "binding.cardNumberLayout") || errorFound
        errorFound = checkDetail(binding.cardCvvEditText.text.toString(), binding.cardCvvLayout, "binding.cardCvvLayout") || errorFound
        errorFound = checkDetail(binding.cardExpirationEditText.text.toString(), binding.cardExpirationLayout, "binding.cardExpirationLayout") || errorFound
        return !errorFound
    }

    private fun checkDetail(text: String, textInputLayout: TextInputLayout, key: String): Boolean {
        return if (text.isEmpty()) { //error found
            textInputLayout.error = "Card detail is empty!"
            true
        } else {
            sp.edit().putString(key, text).commit()
            false
        }
    }

    private fun navigateBackToCardsScreen() {
        val direction = AddCardFragmentDirections.actionAddCardFragmentToCardsFragment()
        requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
    }

    private fun openCardsList() {
        val cardArray = getAllCards().toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_card_cards_dialog_title)
            .setAdapter(CardsAdapter(requireContext(), cardArray)) { _, which ->
                val selectedCard = cardArray[which]
                binding.card = selectedCard
            }
            .show()
    }

    private fun getAllCards(): List<GiftCard> = listOf(GiftCard.MAX, GiftCard.CORPORATE, GiftCard.HOT)
}


class CardsAdapter(context: Context, private val cardsList: Array<out GiftCard>) :
    ArrayAdapter<GiftCard>(context, R.layout.card_dialog_row, cardsList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: CardDialogRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_dialog_row, parent, false)
        binding.card = cardsList[position]
        return binding.root
    }
}