package com.example.composefirsttry.giftcard.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.logic.stores.repository.StoresRepo
import com.example.composefirsttry.giftcard.ui.main.cardutils.CardModel
import com.example.composefirsttry.giftcard.ui.main.states.StoreMainState
import com.example.composefirsttry.giftcard.ui.main.states.StoresMainIntention
import com.example.composefirsttry.giftcard.utils.CardUtils
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.SPKeys
import com.example.composefirsttry.utils.observeForeverFreshly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class StoresMainViewModel @Inject constructor (
    private val sp: SharedPreferences,
    private val storesRepo: StoresRepo,
    private val cardsRepo: CardsRepo
) : ViewModel() {

    var maxCardChecked: Boolean = sp.getBoolean(SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE, true)
    var corporateCardChecked: Boolean = sp.getBoolean(SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE, true)
    var hotCardChecked: Boolean = sp.getBoolean(SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE, true)

    val searchTextMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    private lateinit var searchTextMutableLiveDataObserver: Observer<String>

    private lateinit var storesLiveData: LiveData<List<Store>>
    private lateinit var storesLiveDataObserver: Observer<List<Store>>

    private var stateMutableLiveData = MutableLiveData<StoreMainState>()

    private lateinit var cardsLiveData: LiveData<CardModel>
    private lateinit var cardsLiveDataObserver: Observer<CardModel>

    init {
        initListeners()
    }

    private fun initListeners() {
        //attach viewModel's stores to db
        storesLiveData = Transformations.map(storesRepo.getAllStoresDb()) { storesEntities ->
            storesEntities.map { storeEntity ->
                DbToModelConverter.fromEntityToStore(storeEntity)
            }
        }
        //notify when changes happens
        storesLiveDataObserver = storesLiveData.observeForeverFreshly(Observer { ignore ->
            sendFreshData()
        })

        //attach viewModel's cards to db
        cardsLiveData = Transformations.map(cardsRepo.getAllCardsDb()) { cardsEntities ->
            fun transformEntitiesToGiftCards(entities: List<CardEntity>): List<GiftCard> {
                return entities.map { cardEntity -> DbToModelConverter.getGiftCard(cardEntity) }
            }
            fun fromGiftCardsToCardModel(giftCards: List<GiftCard>): CardModel {
                val cardModel = CardModel()
                giftCards.forEach { giftCard -> cardModel.addCard(giftCard)}
                return cardModel
            }
            val giftCards = transformEntitiesToGiftCards(cardsEntities)
            return@map fromGiftCardsToCardModel(giftCards)
        }
        //notify when changes happens
        cardsLiveDataObserver = cardsLiveData.observeForeverFreshly(Observer { ignore ->
            sendFreshData()
        })

        searchTextMutableLiveDataObserver = searchTextMutableLiveData.observeForeverFreshly(Observer { textFilter ->
            action(StoresMainIntention.FilterByPrefix(textFilter))
        })
    }

    override fun onCleared() {
        super.onCleared()
        searchTextMutableLiveData.removeObserver(searchTextMutableLiveDataObserver)
        storesLiveData.removeObserver(storesLiveDataObserver)
        cardsLiveData.removeObserver(cardsLiveDataObserver)
    }

    //NOTE: Use this way, because LiveData stores events and triggers them once new LifecycleOwner is observe to them.
    // ViewModel doesn't create new Livedata on backpress, but the fragment has new LifecycleOwner - this cause UX bug.
    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<StoreMainState>) {
        stateMutableLiveData = MutableLiveData<StoreMainState>()
        stateMutableLiveData.observe(owner, observer)
    }

    private fun sendFreshData() {
        L.i("sendFreshData")
        stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), getFilteredStores()))
    }

    fun action(intention: StoresMainIntention){
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is StoresMainIntention.FilterByPrefix -> filterByPrefix(intention.prefix)
                is StoresMainIntention.FilterByCard -> filterByCard(intention.giftCardType, intention.isChecked)
                is StoresMainIntention.Refresh -> {
                    stateMutableLiveData.postValue(StoreMainState.Waiting)
                    if (isFirstTimeDataFetched()) {
                        storesRepo.refresh()
                    } else {
                        stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), getFilteredStores()))
                    }
                }
                is StoresMainIntention.FilterBySelectedStores -> filterBySelectedStores()
                is StoresMainIntention.ClearStoresSelection -> clearStoresSelection()
                is StoresMainIntention.SelectStore -> storeSelected(intention.store)
                is StoresMainIntention.OpenStoreDialog -> openStoreDialog(intention.store)
                is StoresMainIntention.AddStoreToFavorites -> addStoreToFavorites(intention.store)
                is StoresMainIntention.NavigateToCardsScreen -> navigateToCardsScreen(intention.giftCardType)
                is StoresMainIntention.CheckCardDiscount -> checkCardDiscount(intention.giftCardType)
                else -> L.e("Unfamiliar intention. Intention = ${intention.javaClass.simpleName}")
            }
        }
    }

    private fun checkCardDiscount(giftCardType: GiftCardType) {
        val cards = getCardModel().getCards(giftCardType)
        stateMutableLiveData.postValue(StoreMainState.DisplayToast(cards, cards.size == 1))
    }

    private fun navigateToCardsScreen(giftCardType: GiftCardType) {
        stateMutableLiveData.postValue(StoreMainState.Navigation.NavigateToCardsScreen(giftCardType))
    }

    private fun addStoreToFavorites(store: Store) {
        storesRepo.addStoreToFavorites(DbToModelConverter.fromStoreToEntity(store))
    }

    private fun openStoreDialog(store: Store) {
        stateMutableLiveData.postValue(StoreMainState.StoreDialogOpened(store))
    }

    private fun clearStoresSelection() {
        L.i("clearStoresSelection: storesHasBeenSelected= $storesHasBeenSelected")
        if (storesHasBeenSelected == ACTIVE) { //deactivate
            setStoresSelection(VISIBLE)
            stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), getFilteredStores()))
        } else { //make invisible
            getStores().forEach { it.selected = false }
            setStoresSelection(INVISIBLE)
            stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), getFilteredStores(), hideStoreSelectionFilter = true))
        }
    }

    private fun storeSelected(store: Store) {
        store.selected = getStoreNewState(store)

        when {
            storesHasBeenSelected == INVISIBLE -> {
                setStoresSelection(VISIBLE)
                stateMutableLiveData.postValue(StoreMainState.StoreSelected(store, true))
            }
            getStores().any { it.selected } -> {
                stateMutableLiveData.postValue(StoreMainState.StoreSelected(store, true))
            }
            storesHasBeenSelected == VISIBLE -> {
                setStoresSelection(INVISIBLE)
                stateMutableLiveData.postValue(StoreMainState.StoreSelected(store, false))
            }
            storesHasBeenSelected == ACTIVE -> {
                setStoresSelection(INVISIBLE)
                stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), getFilteredStores(), hideStoreSelectionFilter = true))
            }
        }
    }

    private fun getStoreNewState(store: Store): Boolean = !store.selected //get reverse store selection state

    private fun setStoresSelection(selectionState: Int) {
        L.i("setStoreSelection: selectionState= $selectionState")
        storesHasBeenSelected = selectionState
    }

    private fun getFilteredStores(): List<Store> = getStores().filter { store -> shouldStoreBeDisplayed(store, searchTextMutableLiveData.value!!) }

    private fun filterBySelectedStores() {
        L.i("filterBySelectedStores")
        setStoresSelection(ACTIVE)
        stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), getFilteredStores()))
    }

    private fun filterByCard(giftCardType: GiftCardType, isChecked: Boolean) {
        L.i("filterByCard: card= ${CardUtils.getCardName(giftCardType)}, isChecked= $isChecked")
        sp.edit().putBoolean(getCardSp(giftCardType), isChecked).commit()
        when (giftCardType) {
            GiftCardType.MAX -> maxCardChecked = isChecked
            GiftCardType.ISRACARD -> corporateCardChecked = isChecked
            GiftCardType.TAV_HAHAM -> hotCardChecked = isChecked
        }

        stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), getFilteredStores()))
    }

    private fun filterByPrefix(prefix: String) {
        val filteredStored: List<Store> = getStores().filter { store -> shouldStoreBeDisplayed(store, prefix) }
        stateMutableLiveData.postValue(StoreMainState.DisplayData(getCardModel(), filteredStored))
    }

    private fun shouldStoreBeDisplayed(store: Store, prefix: String): Boolean {
        val cardModel = getCardModel()
        return if ((cardModel.hasCard(GiftCardType.MAX) && store.maxCard && maxCardChecked) ||
            (cardModel.hasCard(GiftCardType.ISRACARD) && store.corporateCard && corporateCardChecked) ||
            (cardModel.hasCard(GiftCardType.TAV_HAHAM) && store.hotCard && hotCardChecked)) {
            (store.selected || storesHasBeenSelected != ACTIVE) //equivalent to: storesHasBeenSelected == ACTIVE -> store.selected
                    && doesStoreNameStartWithPrefix(store.storeName.lowercase(), prefix.lowercase())
        } else {
            false
        }
    }

    private fun doesStoreNameStartWithPrefix(storeName: String, complexPrefix: String): Boolean {
        val acceptablePrefixes: List<String> = complexPrefix.split("||").map { it.trim() }
        return acceptablePrefixes.any { storeName.startsWith(it) }
    }

    private fun getCardSp(giftCardType: GiftCardType): String = when (giftCardType) {
            GiftCardType.MAX -> SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE
            GiftCardType.ISRACARD -> SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE
            GiftCardType.TAV_HAHAM -> SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE
            else -> throw Exception("Unfamiliar GiftCard type!! (${CardUtils.getCardName(giftCardType)})")
    }

    private fun getStores(): List<Store> = storesLiveData.value ?: emptyList()

    private fun getCardModel(): CardModel = cardsLiveData.value ?: CardModel()

    private fun isFirstTimeDataFetched(): Boolean = firstTimeFetchData.getAndSet(false)

    companion object {
        private var firstTimeFetchData: AtomicBoolean = AtomicBoolean(true)

        private const val INVISIBLE = 0
        private const val VISIBLE = 1
        private const val ACTIVE = 2

        private var storesHasBeenSelected: Int = INVISIBLE
    }
}

/* //Just for learning
class StoresMainViewModelFactory(
    private val fragmentViewLifecycleOwner: LifecycleOwner) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StoresMainViewModel(fragmentViewLifecycleOwner) as T
    }
}*/
