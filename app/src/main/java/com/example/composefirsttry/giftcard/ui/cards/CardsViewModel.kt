package com.example.composefirsttry.giftcard.ui.cards

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.ui.cards.states.CardsIntention
import com.example.composefirsttry.giftcard.ui.cards.states.CardsState
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.observeForeverFreshly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val cardsRepo: CardsRepo,
) : ViewModel() {

    private var stateMutableLiveData = MutableLiveData<CardsState>()

    private lateinit var cardsLiveData: LiveData<List<GiftCard>>
    private lateinit var cardsLiveDataObserver: Observer<List<GiftCard>>

    private var navigationEventType: CardsIntention.NavigatedType = CardsIntention.NavigatedType.ShowAll

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
            if (cards.isEmpty())
                stateMutableLiveData.postValue(CardsState.Navigation.NavigateToLandingScreen)
            else
                stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), shouldShowClearAll()))
        })
    }

    override fun onCleared() {
        super.onCleared()
        cardsLiveData.removeObserver(cardsLiveDataObserver)
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
                is CardsIntention.NavigateOutsideToLoadMoney -> navigateOutsideToLoadMoney(intention.card)
                else -> L.e("Unfamiliar CardsIntention (${intention.javaClass.simpleName})")
            }
        }
    }

    private fun navigateOutsideToLoadMoney(card: GiftCard) {
        when (card.type) {
            GiftCardType.MAX -> stateMutableLiveData.postValue(CardsState.Navigation.NavigateOutsideToMax("com.ideomobile.leumicard"))
            GiftCardType.ISRACARD -> stateMutableLiveData.postValue(CardsState.Navigation.NavigateOutsideToIsracard("https://service.isracard.co.il/isracard/externals?reqName=GiftCardCharging_934"))
            GiftCardType.TAV_HAHAM -> stateMutableLiveData.postValue(CardsState.Navigation.NavigateOutsideToTavHaham("com.hot.benefits"))
        }
    }

    private fun refreshData() {
        if (receiveCardClickEvent()) { //display only the selected card
            val cards = getAllCards().filter { it.type == (navigationEventType as CardsIntention.NavigatedType.SingleCard).argCardType }
            stateMutableLiveData.postValue(CardsState.DisplayData(cards, showClearAll = true))
        } else { //regular
            stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), showClearAll = false))
        }
    }

    private fun receiveCardClickEvent(): Boolean = when (navigationEventType) {
        CardsIntention.NavigatedType.ShowAll -> false
        is CardsIntention.NavigatedType.SingleCard -> true
    }

    private fun shouldShowClearAll(): Boolean = receiveCardClickEvent()

    fun setArgCardType(argCardType: GiftCardType?) {
        if (argCardType == null) this.navigationEventType = CardsIntention.NavigatedType.ShowAll
        else this.navigationEventType = CardsIntention.NavigatedType.SingleCard(argCardType = argCardType)
    }

    private fun clearAll() {
        navigationEventType = CardsIntention.NavigatedType.ShowAll
        stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), showClearAll = false))
    }

    private fun removeCard(card: GiftCard) {
        cardsRepo.removeCard(card.id)
    }

    private fun getAllCards(): List<GiftCard> = cardsLiveData.value ?: listOf()
}