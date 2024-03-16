package com.example.composefirsttry.giftcard.ui.addcard

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardExtended
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet.SheetItem
import com.example.composefirsttry.giftcard.logic.cardsmetadata.repository.MetadataCardsRepo
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardIntention
import com.example.composefirsttry.giftcard.ui.addcard.states.AddCardState
import com.example.composefirsttry.giftcard.ui.addcard.utils.AddCardValidator
import com.example.composefirsttry.giftcard.ui.common.dialog.advancedialog.CustomDialogAdapterItem
import com.example.composefirsttry.giftcard.utils.CardUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val cardsRepo: CardsRepo,
    private val metadataCardsRepo: MetadataCardsRepo
) : ViewModel() {

    private val validator = AddCardValidator()

    //just for 2-way binding with the UI
    val nameMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val discountMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val numberMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val cvvMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    val expirationDateMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")

    private val fieldsMutableLiveDataMap: HashMap<CardFieldType, MutableLiveData<String>> = hashMapOf()

    private var navigatedEvent: AddCardIntention.NavigatedType = AddCardIntention.NavigatedType.AddCard

    var cardType: GiftCardType? = null

    private var stateMutableLiveData = MutableLiveData<AddCardState>()

    init {
        fieldsMutableLiveDataMap[CardFieldType.Name] = nameMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.Discount] = discountMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.Number] = numberMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.Cvv] = cvvMutableLiveData
        fieldsMutableLiveDataMap[CardFieldType.ExpirationDate] = expirationDateMutableLiveData
    }

    fun setArgs(card: GiftCard?, fragmentId: Int) {
        navigatedEvent = when {
            !hasReceivedCardInput(card) -> AddCardIntention.NavigatedType.AddCard
            fragmentId == AddCardFragment.CARDS_SCREEN -> AddCardIntention.NavigatedType.EditCardFromCards(card!!)
            fragmentId == AddCardFragment.CARD_DETAILS_SCREEN -> AddCardIntention.NavigatedType.EditCardFromCardDetails(card!!)
            else -> throw Exception("Unfamiliar use case (fragmentId = $fragmentId)")
        }
    }

    private fun hasReceivedCardInput(card: GiftCard?) = card != null

    //NOTE: Use this way, because LiveData stores events and triggers them once new LifecycleOwner is observe to them.
    // ViewModel doesn't create new Livedata on backpress, but the fragment has new LifecycleOwner - this cause UX bug.
    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<AddCardState>) {
        stateMutableLiveData = MutableLiveData<AddCardState>()
        stateMutableLiveData.observe(owner, observer)
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
        when (val event = navigatedEvent) {
            is AddCardIntention.NavigatedType.EditCardFromCards -> editCard(event.card.id)
            is AddCardIntention.NavigatedType.EditCardFromCardDetails -> editCard(event.card.id)
        }
    }

    private fun editCard(cardId: Int) {
        val giftCardExtended: GiftCardExtended = cardsRepo.getCard(cardId)
        val fieldsValueMap: HashMap<CardFieldType, String> = hashMapOf()
        fieldsValueMap[CardFieldType.Name] = giftCardExtended.name
        fieldsValueMap[CardFieldType.Discount] = giftCardExtended.discount.toString()
        fieldsValueMap[CardFieldType.Number] = giftCardExtended.number
        fieldsValueMap[CardFieldType.Cvv] = giftCardExtended.cvv
        fieldsValueMap[CardFieldType.ExpirationDate] = giftCardExtended.expirationDate
        cardType = giftCardExtended.type
        stateMutableLiveData.postValue(AddCardState.DisplayDataEditCard(fieldsValueMap, cardType!!))
    }

    private fun pickCardType(cardType: GiftCardType) {
        this.cardType = cardType
        stateMutableLiveData.postValue(AddCardState.CardImageChanged(cardType))
    }

    private fun openCardsDialog() {
        val listMetadataCardsDb: List<SheetItem> = metadataCardsRepo.getMetadataCardsDb()
        //convert to CustomDialogAdapterItem:
        val dialogAdapterItems = listMetadataCardsDb.map { metadata -> CustomDialogAdapterItem(metadata.type, metadata.imageUrl) }
        stateMutableLiveData.postValue(AddCardState.CardsDialogOpened(dialogAdapterItems))
    }

    private fun focusCardField(fieldType: CardFieldType, hasFocus: Boolean) {
        if (hasFocus && !validator.isFieldStatusOk(fieldType)) {
            stateMutableLiveData.postValue(AddCardState.FieldStatusChanged(fieldType, statusOk = true))
        }
    }

    private fun saveCard(forceSave: Boolean) {
        validator.validateCardFields(getFieldsValue())

        if (validator.isCardDetailsOk() || (forceSave && validator.isCardDetailsPartialFailed())) {
            val giftCardExtended: GiftCardExtended = collectGiftCardData()
            when(val event = navigatedEvent) {
                AddCardIntention.NavigatedType.AddCard -> {
                    cardsRepo.addCard(giftCardExtended)
                    stateMutableLiveData.postValue(AddCardState.Navigation.NavigateBackToCardsScreen)
                }
                is AddCardIntention.NavigatedType.EditCardFromCards -> {
                    cardsRepo.editCard(giftCardExtended.apply { id = event.card.id })
                    stateMutableLiveData.postValue(AddCardState.Navigation.NavigateBackToCardsScreen)
                }
                is AddCardIntention.NavigatedType.EditCardFromCardDetails -> {
                    cardsRepo.editCard(giftCardExtended.apply { id = event.card.id })
                    stateMutableLiveData.postValue(AddCardState.Navigation.NavigateBackToCardDetails(giftCardExtended))
                }
            }
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

    private fun collectGiftCardData(): GiftCardExtended {
        return GiftCardExtended(
            type = cardType!!,
            name = fieldsMutableLiveDataMap.getValue(CardFieldType.Name).value!!,
            imageRes = CardUtils.getCardImage(cardType!!),
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