package com.example.composefirsttry.giftcard.ui.addcard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.AddCardFragmentBinding
import com.example.composefirsttry.databinding.CardDialogRowBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardIntention
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardState
import com.example.composefirsttry.giftcard.utils.CardUtils
import com.example.composefirsttry.utils.requireActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddCardFragment : Fragment() {
    private val viewModel: AddCardViewModel by viewModels()
    private lateinit var binding: AddCardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_card_fragment, container, false)!!
        sendArgsToViewModel()
        binding.model = viewModel
        return binding.root
    }

    private fun sendArgsToViewModel() {
        val args = arguments?.get("card")
        if (args != null) {
            val fragmentId: Int = arguments?.get("fragmentId") as Int
            viewModel.setArgs(args as GiftCard, fragmentId)
            arguments?.clear()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Important fix: removeObservers and getViewLifecyclerOwner instead of activity to prevent multiple call to onChanged from unremoved observers.
        //More info: https://blog.usejournal.com/observe-livedata-from-viewmodel-in-fragment-fd7d14f9f5fb

        viewModel.observeStateLiveData(viewLifecycleOwner, Observer { state ->
            if (state is AddCardState.Navigation) navigate(state)
            else render(state)
        })

        viewModel.action(AddCardIntention.Refresh)
    }

    override fun onStart() {
        super.onStart()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(false)
        binding.saveCard.setOnClickListener { viewModel.action(AddCardIntention.SaveCard()) }
        binding.cardImageLayout.setOnClickListener { viewModel.action(AddCardIntention.OpenCardsDialog) }
        binding.selectCard.setOnClickListener { viewModel.action(AddCardIntention.OpenCardsDialog) }
        setCardDetailListener(CardFieldType.Name, binding.cardNameEditText)
        setCardDetailListener(CardFieldType.Discount, binding.cardDiscountEditText)
        setCardDetailListener(CardFieldType.Number, binding.cardNumberEditText)
        setCardDetailListener(CardFieldType.Cvv, binding.cardCvvEditText)
        setCardDetailListener(CardFieldType.ExpirationDate, binding.cardExpirationEditText)
    }

    override fun onStop() {
        super.onStop()
        requireActivity<GiftCardMainActivity>().displayBottomNavigation(true)
    }

    private fun setCardDetailListener(fieldType: CardFieldType, cardDetailEditText: TextInputEditText) {
        cardDetailEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.action(AddCardIntention.FocusCardField(fieldType, hasFocus))
        }
    }

    private fun navigate(navigationIntention: AddCardState.Navigation) {
        when (navigationIntention) {
            AddCardState.Navigation.NavigateBackToCardsScreen -> {
                val direction = AddCardFragmentDirections.actionAddCardFragmentToCardsFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            is AddCardState.Navigation.NavigateBackToCardDetails -> {
                val direction = AddCardFragmentDirections.actionAddCardFragmentToCardDetailsFragment(navigationIntention.card)
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
            else -> {
                L.e("Unfamiliar navigation (intention: ${navigationIntention.javaClass.simpleName})")
            }
        }
    }

    private fun render(state: AddCardState) {
        when (state) {
            is AddCardState.DisplayData -> displayData(state)
            AddCardState.CardsDialogOpened -> openCardsList()
            is AddCardState.CardImageChanged -> changedCardImage(state.cardType)
            is AddCardState.FieldStatusChanged -> changeFieldStatus(state.fieldType, state.statusOk)
            is AddCardState.DisplayDataEditCard -> displayDataEditCard(state.fieldsValueMap, state.cardType)
        }
    }

    private fun displayDataEditCard(fieldsValueMap: HashMap<CardFieldType, String>, cardType: GiftCardType) {
        binding.cardNameEditText.setText(fieldsValueMap.getValue(CardFieldType.Name))
        binding.cardDiscountEditText.setText(fieldsValueMap.getValue(CardFieldType.Discount))
        binding.cardNumberEditText.setText(fieldsValueMap.getValue(CardFieldType.Number))
        binding.cardCvvEditText.setText(fieldsValueMap.getValue(CardFieldType.Cvv))
        binding.cardExpirationEditText.setText(fieldsValueMap.getValue(CardFieldType.ExpirationDate))
        changedCardImage(cardType = cardType)
        binding.saveCard.setText(R.string.cards_edit_card_dialog_edit_button_text)
    }

    private fun displayData(state: AddCardState.DisplayData) {
        state.fieldsStatusMap.forEach {
            entry -> changeFieldStatus(fieldType = entry.key, statusOk = entry.value.isFieldStatusOk())
        }
        if (state.fieldsStatusMap.getValue(CardFieldType.Image).isFieldStatusFailed()) {
            binding.showCardErrorSign = true
        }
        if (state.displayPartialErrorDialog) {
            displayPartialErrorDialog()
        }
        binding.saveCard.setText(R.string.add_card_button)
    }

    private fun changeFieldStatus(fieldType: CardFieldType, statusOk: Boolean) {
        val errorMessage: String? = if (statusOk) null else "Card detail is empty!"
        when (fieldType) {
            is CardFieldType.Name -> binding.cardNameLayout.error = errorMessage
            is CardFieldType.Discount -> binding.cardDiscountLayout.error = errorMessage
            is CardFieldType.Number -> binding.cardNumberLayout.error = errorMessage
            is CardFieldType.Cvv -> binding.cardCvvLayout.error = errorMessage
            is CardFieldType.ExpirationDate -> binding.cardExpirationLayout.error = errorMessage
            else -> L.e("Unfamiliar fieldType (${fieldType.javaClass.simpleName})")
        }
    }

    private fun displayPartialErrorDialog() {
        android.app.AlertDialog.Builder(requireActivity())
            .setTitle(R.string.add_card_save_card_dialog_title)
            .setMessage(R.string.add_card_save_card_dialog_message)
            .setNeutralButton(R.string.add_card_save_card_dialog_yes_button_text) { _, _ -> viewModel.action(AddCardIntention.SaveCard(forceSave = true)) }
            .setNegativeButton(R.string.add_card_save_card_dialog_cancel_button_text) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun changedCardImage(cardType: GiftCardType) {
        binding.showConcreteCard = true
        binding.showCardErrorSign = false
        binding.imageRes = CardUtils.getCardImage(cardType)
    }

    private fun openCardsList() {
        val cardArray = getAllCards().toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_card_cards_dialog_title)
            .setAdapter(CardsAdapter(requireContext(), cardArray)) { _, which ->
                val selectedCard = cardArray[which]
                viewModel.action(AddCardIntention.PickCardType(selectedCard))
            }
            .show()
    }

    private fun getAllCards(): List<GiftCardType> = listOf(GiftCardType.MAX, GiftCardType.ISRACARD, GiftCardType.TAV_HAHAM)

    companion object {
        // TODO: 31/10/2022 move to some utils location
        const val CARDS_SCREEN = 1
        const val CARD_DETAILS_SCREEN = 2
    }
}


class CardsAdapter(context: Context, private val cardsList: Array<out GiftCardType>) :
    ArrayAdapter<GiftCardType>(context, R.layout.card_dialog_row, cardsList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: CardDialogRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_dialog_row, parent, false)
        binding.cardType = cardsList[position]
        return binding.root
    }
}