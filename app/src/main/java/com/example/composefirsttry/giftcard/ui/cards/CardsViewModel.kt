package com.example.composefirsttry.giftcard.ui.cards

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.logic.shoppingclubs.model.ShoppingClub
import com.example.composefirsttry.giftcard.logic.shoppingclubs.repository.ShoppingClubsRepo
import com.example.composefirsttry.giftcard.ui.cards.states.CardsIntention
import com.example.composefirsttry.giftcard.ui.cards.states.CardsState
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val cardsRepo: CardsRepo,
    private val shoppingClubRepo: ShoppingClubsRepo
) : ViewModel() {

    private var stateMutableLiveData = MutableLiveData<CardsState>()

    private var cardsLiveData: LiveData<List<GiftCard>>? = MutableLiveData<List<GiftCard>>()

    private var _cardsMediatorDb: MediatorLiveData<List<GiftCard>>? = null
    private val cardsMediatorDb get() = _cardsMediatorDb!!

    private var navigatedEvent: CardsIntention.NavigatedType = CardsIntention.NavigatedType.ShowAll

    fun startObservingDb(owner: LifecycleOwner) {
        //attach viewModel's cards to db
        cardsLiveData = cardsRepo.getAllCardsDb().map { cardsEntities ->
            cardsEntities.map { cardEntity -> DbToModelConverter.getGiftCard(cardEntity) }
        }
        //notify when changes happens
        _cardsMediatorDb = MediatorLiveData<List<GiftCard>>()
        cardsMediatorDb.addSource(cardsLiveData!!) { cards ->
            L.i("CardsViewModel - cardsLiveDataObserver - timestamp = ${System.currentTimeMillis()}")
            if (cards.isEmpty())
                stateMutableLiveData.postValue(CardsState.Navigation.NavigateToLandingScreen)
            else {
                refreshData()
            }
        }

        cardsMediatorDb.observe(owner) {}
    }

    fun stopObservingDb(owner: LifecycleOwner) {
        cardsMediatorDb.removeObservers(owner)
        _cardsMediatorDb = null
        cardsLiveData = null
    }

    //NOTE: Use this way, because LiveData stores events and triggers them once new LifecycleOwner is observe to them.
    // ViewModel doesn't create new Livedata on backpress, but the fragment has new LifecycleOwner - this cause UX bug.
    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<CardsState>) {
        stateMutableLiveData = MutableLiveData<CardsState>()
        stateMutableLiveData.observe(owner, observer)
    }

    fun action(intention: CardsIntention) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is CardsIntention.OpenRemoveCardDialog -> stateMutableLiveData.postValue(CardsState.RemoveCardDialogOpened(intention.card))
                is CardsIntention.RemoveCard -> removeCard(intention.card)
                CardsIntention.ClearAll -> clearAll()
                CardsIntention.Refresh -> refreshData()
                is CardsIntention.NavigateToEditCard -> stateMutableLiveData.postValue(CardsState.Navigation.NavigateToEditCard(intention.card))
                is CardsIntention.NavigateToCardDetails -> stateMutableLiveData.postValue(CardsState.Navigation.NavigateToCardDetails(intention.card))
                is CardsIntention.NavigateOutsideToLoadMoney -> navigateOutsideToLoadMoney(intention.card)
                else -> throw Exception("Unfamiliar CardsIntention (${intention.javaClass.simpleName})")
            }
        }
    }

    private fun navigateOutsideToLoadMoney(card: GiftCard) {
        val club = getShoppingClubs().find { shoppingClub -> shoppingClub.clubId == card.cardClubId}!!
        if (club.loadThroughApp) stateMutableLiveData.postValue(CardsState.Navigation.NavigateOutsideToApplication(club.loadMoneyAddress))
        else stateMutableLiveData.postValue(CardsState.Navigation.NavigateOutsideToWebsite(club.loadMoneyAddress))
    }

    private fun refreshData() {
        when (val event = navigatedEvent) {
            is CardsIntention.NavigatedType.SingleCardType -> {
                val cards = getAllCards().filter { it.cardClubId == event.argClubId }
                stateMutableLiveData.postValue(CardsState.DisplayData(cards, showClearAll = true))
            }
            CardsIntention.NavigatedType.ShowAll -> stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), showClearAll = false))
        }
    }

    fun setArgCardClubId(argClubId: String?) {
        if (argClubId == null) this.navigatedEvent = CardsIntention.NavigatedType.ShowAll
        else this.navigatedEvent = CardsIntention.NavigatedType.SingleCardType(argClubId)
    }

    private fun clearAll() {
        navigatedEvent = CardsIntention.NavigatedType.ShowAll
        stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), showClearAll = false))
    }

    private fun removeCard(card: GiftCard) {
        cardsRepo.removeCard(card.id)
    }

    private fun getAllCards(): List<GiftCard> = cardsLiveData?.value ?: listOf()

    private fun getShoppingClubs(): List<ShoppingClub> = shoppingClubRepo.getAllExistingShoppingClubs().value!!.map { clubEntity ->
        val shoppingClub = DbToModelConverter.fromEntityToShoppingClub(clubEntity)
        shoppingClub
    }
}