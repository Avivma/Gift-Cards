package com.example.composefirsttry.giftcard.ui.addcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardIntention
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardState
import com.example.composefirsttry.giftcard.ui.addcard.utils.AddCardValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val cardsRepo: CardsRepo
) : ViewModel() {

    private val validator = AddCardValidator()

    //just for 2-way binding with the UI
    val nameMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val discountMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val numberMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val cvvMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val expirationDateMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")

    private val fieldsMutableLiveDataMap: HashMap<CardFieldType, MutableLiveData<String>> = hashMapOf()

    private var argCard: GiftCard? = null // TODO: 24/10/2022 change to event

    var cardType: GiftCardType? = null

    private val stateMutableLiveData = MutableLiveData<AddCardState>()
    val stateLiveData: LiveData<AddCardState> = stateMutableLiveData

    private val navigationMutableLiveData = MutableLiveData<AddCardIntention>()
    val navigationLiveData: LiveData<AddCardIntention> = navigationMutableLiveData

    init {
        fieldsMutableLiveDataMap[CardFieldType.Name] = nameMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.Discount] = discountMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.Number] = numberMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.Cvv] = cvvMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.ExpirationDate] = expirationDateMutableLiveData
    }

    fun setArgCard(card: GiftCard?) {
        this.argCard = card
    }

    fun action(intention: AddCardIntention) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is AddCardIntention.SaveCard -> saveCard(intention.forceSave)
                is AddCardIntention.FocusCardField -> focusCardField(intention.fieldType, intention.hasFocus)
                is AddCardIntention.OpenCardsDialog -> openCardsDialog()
                is AddCardIntention.PickCardType -> pickCardType(intention.cardType)
                is AddCardIntention.Refresh -> refresh()
                else -> throw Exception("unfamiliar intention (intention = ${intention.javaClass.simpleName})")
            }
        }
    }

    private fun refresh() {
        if (argCard != null) {
            editCard(argCard!!)
        }
    }

    private fun editCard(giftCard: GiftCard) {
        val fieldsValueMap: HashMap<CardFieldType, String> = hashMapOf()
        fieldsValueMap[CardFieldType.Name] = giftCard.name
        fieldsValueMap[CardFieldType.Discount] = giftCard.discount.toString()
        fieldsValueMap[CardFieldType.Number] = giftCard.number
        fieldsValueMap[CardFieldType.Cvv] = giftCard.cvv
        fieldsValueMap[CardFieldType.ExpirationDate] = giftCard.expirationDate
        cardType = giftCard.type
        stateMutableLiveData.postValue(AddCardState.DisplayDataEditCard(fieldsValueMap, cardType!!))
    }

    private fun pickCardType(cardType: GiftCardType) {
        this.cardType = cardType
        stateMutableLiveData.postValue(AddCardState.CardImageChanged(cardType))
    }

    private fun openCardsDialog() {
        stateMutableLiveData.postValue(AddCardState.CardsDialogOpened)
    }

    private fun focusCardField(fieldType: CardFieldType, hasFocus: Boolean) {
        if (hasFocus && !validator.isFieldStatusOk(fieldType)) {
            stateMutableLiveData.postValue(AddCardState.FieldStatusChanged(fieldType, statusOk = true))
        }
    }

    private fun saveCard(forceSave: Boolean) {
        validator.validateCardFields(getFieldsValue())

        if (validator.isCardDetailsOk() || (forceSave && validator.isCardDetailsPartialFailed())) {
            val giftCard: GiftCard = collectGiftCard()
            cardsRepo.addCard(giftCard)
            navigationMutableLiveData.postValue(AddCardIntention.Navigation.NavigateBackToCardsScreen)
        } else {
            checkForCardDetailsErrors(validator)
        }
    }

    private fun getFieldsValue(): HashMap<CardFieldType, String> {
        val fieldsValueMap: HashMap<CardFieldType, String> = hashMapOf()
        fieldsValueMap[CardFieldType.Image] = (cardType != null).toString()
        for (entry in fieldsMutableLiveDataMap) {
            fieldsValueMap[entry.key] = entry.value.value!!
        }
        return fieldsValueMap
    }

    private fun collectGiftCard(): GiftCard {
        return GiftCard(
            type = cardType!!,
            name = fieldsMutableLiveDataMap.getValue(CardFieldType.Name).value!!,
            imageRes = GiftCardType.getCardImage(cardType!!),
            discount = fieldsMutableLiveDataMap.getValue(CardFieldType.Discount).value!!.toFloat()
        ).apply {
            number = fieldsMutableLiveDataMap.getValue(CardFieldType.Number).value!!
            cvv = fieldsMutableLiveDataMap.getValue(CardFieldType.Cvv).value!!
            expirationDate = fieldsMutableLiveDataMap.getValue(CardFieldType.ExpirationDate).value!!
        }
    }

    private fun checkForCardDetailsErrors(validator: AddCardValidator) {
        stateMutableLiveData.postValue(
            AddCardState.DisplayData(
                validator.getFieldsStatusMap(),
                displayPartialErrorDialog = validator.isCardDetailsPartialFailed()
            )
        )
    }
}