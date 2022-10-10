package com.example.composefirsttry.giftcard.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.giftcard.model.Store
import com.example.composefirsttry.giftcard.repository.GiftCardRepo
import com.example.composefirsttry.giftcard.ui.main.states.StoreMainState
import com.example.composefirsttry.giftcard.ui.main.states.StoresMainIntention
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
    private val giftCardRepo: GiftCardRepo
) : ViewModel() {

    var maxCardChecked: Boolean = sp.getBoolean(SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE, true)
    var corporateCardChecked: Boolean = sp.getBoolean(SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE, true)
    var hotCardChecked: Boolean = sp.getBoolean(SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE, true)

    val searchTextMutableLiveData: MutableLiveData<String> = MutableLiveData<String>("")
    private lateinit var searchTextMutableLiveDataObserver: Observer<String>

    private lateinit var storesLiveData: LiveData<List<Store>>
    private lateinit var storesLiveDataObserver: Observer<List<Store>>

    private val stateMutableLiveData = MutableLiveData<StoreMainState>()
    val stateLiveData: LiveData<StoreMainState> = stateMutableLiveData

    init {
        initListeners()
    }

    private fun initListeners() {
        //attach viewModel's stores to db
        storesLiveData = Transformations.map(giftCardRepo.getAllStoresDb()) { storesEntities ->
            storesEntities.map { storeEntity ->
                DbToModelConverter.fromEntityToStore(storeEntity)
            }
        }
        //notify when changes happens
        storesLiveDataObserver = storesLiveData.observeForeverFreshly(Observer { ignore ->
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
    }

    private fun sendFreshData() {
        L.i("sendFreshData")
        stateMutableLiveData.postValue(StoreMainState.DisplayData(getFilteredStores()))
    }

    fun action(intention: StoresMainIntention){
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is StoresMainIntention.FilterByPrefix -> filterByPrefix(intention.prefix)
                is StoresMainIntention.FilterByCard -> filterByCard(intention.card, intention.isChecked)
                is StoresMainIntention.Refresh -> {
                    stateMutableLiveData.postValue(StoreMainState.Waiting)
                    if (isFirstTimeDataFetched()) {
                        giftCardRepo.refresh()
                    } else {
                        stateMutableLiveData.postValue(StoreMainState.DisplayData(getStores()))
                    }
                }
                is StoresMainIntention.FilterBySelectedStores -> {
                    filterBySelectedStores()
                }
                is StoresMainIntention.ClearStoresSelection -> {
                    clearStoresSelection()
                }
                is StoresMainIntention.SelectStore -> {
                    storeSelected(intention.store)
                }
                is StoresMainIntention.OpenStoreDialog -> {
                    openStoreDialog(intention.store)
                }
                is StoresMainIntention.AddStoreToFavorites -> {
                    addStoreToFavorites(intention.store)
                }
                else -> L.e("Unfamiliar intention. Intention = ${intention.javaClass.simpleName}")
            }
        }
    }

    private fun addStoreToFavorites(store: Store) {
        giftCardRepo.addStoreToFavorites(DbToModelConverter.fromStoreToEntity(store))
    }

    private fun openStoreDialog(store: Store) {
        stateMutableLiveData.postValue(StoreMainState.StoreDialogOpened(store))
    }

    private fun clearStoresSelection() {
        L.i("clearStoresSelection: storesHasBeenSelected= $storesHasBeenSelected")
        if (storesHasBeenSelected == ACTIVE) { //deactivate
            setStoresSelection(VISIBLE)
            stateMutableLiveData.postValue(StoreMainState.DisplayData(getFilteredStores()))
        } else { //make invisible
            getStores().forEach { it.selected = false }
            setStoresSelection(INVISIBLE)
            stateMutableLiveData.postValue(StoreMainState.DisplayData(getFilteredStores(), hideStoreSelectionFilter = true))
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
                stateMutableLiveData.postValue(StoreMainState.DisplayData(getFilteredStores(), hideStoreSelectionFilter = true))
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
        stateMutableLiveData.postValue(StoreMainState.DisplayData(getFilteredStores()))
    }

    private fun filterByCard(card: GiftCard, isChecked: Boolean) {
        L.i("filterByCard: card= ${card.name}, isChecked= $isChecked")
        sp.edit().putBoolean(getCardSp(card), isChecked).commit()
        when (card) {
            GiftCard.MAX -> maxCardChecked = isChecked
            GiftCard.CORPORATE -> corporateCardChecked = isChecked
            GiftCard.HOT -> hotCardChecked = isChecked
        }

        stateMutableLiveData.postValue(StoreMainState.DisplayData(getFilteredStores()))
    }

    private fun filterByPrefix(prefix: String) {
        val filteredStored: List<Store> = getStores().filter { store -> shouldStoreBeDisplayed(store, prefix) }
        stateMutableLiveData.postValue(StoreMainState.DisplayData(filteredStored))
    }

    private fun shouldStoreBeDisplayed(store: Store, prefix: String): Boolean {
        return if ((store.maxCard && maxCardChecked) ||
            (store.corporateCard && corporateCardChecked) ||
            (store.hotCard && hotCardChecked)) {
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

    private fun getCardSp(card: GiftCard): String = when (card) {
            GiftCard.MAX -> SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE
            GiftCard.CORPORATE -> SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE
            GiftCard.HOT -> SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE
            else -> throw Exception("Unfamiliar GiftCard type!! (${card.name})")
    }

    private fun getStores(): List<Store> = storesLiveData.value ?: emptyList()

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
