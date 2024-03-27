package com.example.composefirsttry.giftcard.ui.carddetails

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardExtended
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.logic.shoppingclubs.model.ShoppingClub
import com.example.composefirsttry.giftcard.logic.shoppingclubs.repository.ShoppingClubsRepo
import com.example.composefirsttry.giftcard.ui.carddetails.state.CardDetailsIntention
import com.example.composefirsttry.giftcard.ui.carddetails.state.CardDetailsState
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.observeForeverFreshly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val cardsRepo: CardsRepo,
    private val shoppingClubRepo: ShoppingClubsRepo
) : ViewModel() {

    private var stateMutableLiveData = MutableLiveData<CardDetailsState>()

    private lateinit var cardsLiveData: LiveData<List<GiftCard>>
    private lateinit var cardsLiveDataObserver: Observer<List<GiftCard>>

    lateinit var cardArg: GiftCardExtended

    init {
        initListeners()
    }

    private fun initListeners() {
        //attach viewModel's cards to db
        cardsLiveData = Transformations.map(cardsRepo.getAllCardsDb()) { cardsEntities ->
            cardsEntities.map { cardEntity -> DbToModelConverter.getGiftCard(cardEntity) }
        }
        //notify when changes happens
        cardsLiveDataObserver = cardsLiveData.observeForeverFreshly(Observer { cards ->
            if (cards.isEmpty()) stateMutableLiveData.postValue(CardDetailsState.Navigation.NavigateToLandingScreen)
            else stateMutableLiveData.postValue(CardDetailsState.Navigation.NavigateBackToCards)
        })
    }

    //NOTE: Use this way, because LiveData stores events and triggers them once new LifecycleOwner is observe to them.
    // ViewModel doesn't create new Livedata on backpress, but the fragment has new LifecycleOwner - this cause UX bug.
    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<CardDetailsState>) {
        stateMutableLiveData = MutableLiveData<CardDetailsState>()
        stateMutableLiveData.observe(owner, observer)
    }

    fun action(intention: CardDetailsIntention) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is CardDetailsIntention.Refresh -> refresh(intention.card)
                CardDetailsIntention.NavigateToEditCard -> stateMutableLiveData.postValue(CardDetailsState.Navigation.NavigateToEditCard(cardArg))
                CardDetailsIntention.OpenRemoveCardDialog -> stateMutableLiveData.postValue(CardDetailsState.RemoveCardDialogOpened)
                CardDetailsIntention.RemoveCard -> removeCard(cardArg)
                CardDetailsIntention.NavigateOutsideToLoadMoney -> navigateOutsideToLoadMoney(cardArg)
                else -> L.e("Unfamiliar CardDetailsIntention (${intention.javaClass.simpleName})")
            }
        }
    }

    private fun refresh(card: GiftCard) {
        val cardDetailsExtended = cardsRepo.getCard(card.id)
        fillEmptyFieldsWithDummy(cardDetailsExtended)
        cardArg = cardDetailsExtended
        stateMutableLiveData.postValue(CardDetailsState.DisplayData(cardDetailsExtended))
    }

    private fun fillEmptyFieldsWithDummy(cardArg: GiftCardExtended) {
        if (cardArg.number.isEmpty()) cardArg.number = "****-****-****-****"
        if (cardArg.cvv.isEmpty()) cardArg.cvv = "***"
        if (cardArg.expirationDate.isEmpty()) cardArg.expirationDate = "MM/YY"
    }

    private fun navigateOutsideToLoadMoney(card: GiftCard) {
        val club = getShoppingClubs().find { shoppingClub -> shoppingClub.clubId == card.cardClubId}!!
        if (club.loadThroughApp) stateMutableLiveData.postValue(CardDetailsState.Navigation.NavigateOutsideToApplication(club.loadMoneyAddress))
        else stateMutableLiveData.postValue(CardDetailsState.Navigation.NavigateOutsideToWebsite(club.loadMoneyAddress))
    }

    override fun onCleared() {
        super.onCleared()
        cardsLiveData.removeObserver(cardsLiveDataObserver)
    }

    private fun removeCard(card: GiftCard) {
        cardsRepo.removeCard(card.id)
    }

    private fun getShoppingClubs(): List<ShoppingClub> = shoppingClubRepo.getAllExistingShoppingClubs().value!!.map { clubEntity ->
        val shoppingClub = DbToModelConverter.fromEntityToShoppingClub(clubEntity)
        shoppingClub
    }
}