package com.example.composefirsttry.giftcard.ui.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.AddCardFragmentBinding
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardIntention
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardState
import com.example.composefirsttry.giftcard.ui.common.dialog.CustomDialog
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem
import com.example.composefirsttry.giftcard.ui.common.dialog.listdialog.ListCustomDialog
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl
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
            is AddCardState.CardsDialogOpened -> openCardsList(state.dialogItems)
            is AddCardState.CardImageChanged -> changedCardImage(state.clubDetails)
            is AddCardState.FieldStatusChanged -> changeFieldStatus(state.fieldType, state.statusOk)
            is AddCardState.DisplayDataEditCard -> displayDataEditCard(state.fieldsValueMap, state.clubDetails)
            else -> throw Exception("Unfamiliar AddCardState (${state.javaClass.simpleName})")
        }
    }

    private fun displayDataEditCard(fieldsValueMap: HashMap<CardFieldType, String>, clubDetails: ClubIdAndUrl) {
        binding.cardNameEditText.setText(fieldsValueMap.getValue(CardFieldType.Name))
        binding.cardDiscountEditText.setText(fieldsValueMap.getValue(CardFieldType.Discount))
        binding.cardNumberEditText.setText(fieldsValueMap.getValue(CardFieldType.Number))
        binding.cardCvvEditText.setText(fieldsValueMap.getValue(CardFieldType.Cvv))
        binding.cardExpirationEditText.setText(fieldsValueMap.getValue(CardFieldType.ExpirationDate))
        changedCardImage(clubDetails)
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
        CustomDialog(requireActivity())
            .setTitle(R.string.add_card_save_card_dialog_title)
            .setMessage(R.string.add_card_save_card_dialog_message)
            .setPositiveButton(R.string.add_card_save_card_dialog_yes_button_text) { viewModel.action(AddCardIntention.SaveCard(forceSave = true)) }
            .setNegativeButton(R.string.add_card_save_card_dialog_cancel_button_text) { }
            .show()
    }

    private fun changedCardImage(clubDetails: ClubIdAndUrl) {
        binding.showConcreteCard = true
        binding.showCardErrorSign = false
        binding.imageUrl = clubDetails.url
    }

    private fun openCardsList(dialogItems: List<CustomDialogAdapterItem>) {
        ListCustomDialog(requireActivity()).apply {
            setTitle(R.string.add_card_cards_dialog_title)
            setAdapter(dialogItems) { item ->
                viewModel.action(AddCardIntention.PickCardType(item))
            }
        }.show()
    }

    companion object {
        // TODO: 31/10/2022 move to some utils location
        const val CARDS_SCREEN = 1
        const val CARD_DETAILS_SCREEN = 2
    }
}