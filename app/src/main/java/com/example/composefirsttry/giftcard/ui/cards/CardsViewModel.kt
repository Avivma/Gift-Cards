package com.example.composefirsttry.giftcard.ui.cards

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.repository.CardEncryptionHandler
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
    private val cardEncryptionHandler: CardEncryptionHandler
) : ViewModel() {
    private val stateMutableLiveData = MutableLiveData<CardsState>()
    val stateLiveData: LiveData<CardsState> = stateMutableLiveData

    private val navigationMutableLiveData = MutableLiveData<CardsIntention.Navigation>()
    val navigationLiveData: LiveData<CardsIntention.Navigation> = navigationMutableLiveData

    private lateinit var cardsLiveData: LiveData<List<GiftCard>>
    private lateinit var cardsLiveDataObserver: Observer<List<GiftCard>>

    private var argCard: GiftCard? = null

    init {
        initListeners()
    }

    private fun initListeners() {
/*        cardsRepo.getCardsExistLiveData.observe(viewLifecycleOwner, Observer { hasCards ->
            if (!hasCards) {
                val direction = CardsFragmentDirections.actionCardsFragmentToLandingFragment()
                requireActivity<GiftCardMainActivity>().getNavController().navigate(direction)
            }
        })*/
        //attach viewModel's cards to db
        cardsLiveData = Transformations.map(cardsRepo.getAllCardsDb()) { cardsEntities ->
            cardsEntities.map { cardEntity ->
                val values = cardEncryptionHandler.getDecryptedValues(cardEntity)
                DbToModelConverter.getGiftCard(cardEntity, values)
            }
        }
        //notify when changes happens
        cardsLiveDataObserver = cardsLiveData.observeForeverFreshly(Observer { cards ->
            if (cards.isEmpty())
                navigationMutableLiveData.postValue(CardsIntention.Navigation.NavigateToLandingScreen)
            else
                stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), shouldShowClearAll()))
        })
    }

    override fun onCleared() {
        super.onCleared()
        cardsLiveData.removeObserver(cardsLiveDataObserver)
    }

    fun action(intention: CardsIntention) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is CardsIntention.OpenRemoveCardDialog -> stateMutableLiveData.postValue(CardsState.RemoveCardDialogOpened(intention.card))
                is CardsIntention.RemoveCard -> removeCard(intention.card)
                is CardsIntention.ClearAll -> clearAll()
                is CardsIntention.Refresh -> refreshData()
                else -> L.e("Unfamiliar CardsIntention (${intention.javaClass.simpleName})")
            }
        }
    }

    private fun refreshData() {
        if (receiveCardClickEvent()) { //display only the selected card
            stateMutableLiveData.postValue(CardsState.DisplayData(listOf(argCard!!), showClearAll = true))
        } else { //regular
            stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), showClearAll = false))
        }
    }

    private fun receiveCardClickEvent(): Boolean = argCard != null

    private fun shouldShowClearAll(): Boolean = receiveCardClickEvent()

    fun setArgCard(argCard: GiftCard?) {
        this.argCard = argCard
    }

    private fun clearAll() {
        argCard = null
        stateMutableLiveData.postValue(CardsState.DisplayData(getAllCards(), showClearAll = false))
    }

    private fun removeCard(card: GiftCard) {
        cardsRepo.removeCard(card)
    }

    private fun getAllCards(): List<GiftCard> = cardsLiveData.value ?: listOf()
}