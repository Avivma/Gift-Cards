package com.example.composefirsttry.giftcard.ui.main

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.MyApplication
import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.giftcard.model.Store
import com.example.composefirsttry.giftcard.repository.GiftCardRepo
import com.example.composefirsttry.giftcard.ui.main.states.StoreMainState
import com.example.composefirsttry.giftcard.ui.main.states.StoresMainIntention
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.SPKeys
import com.example.composefirsttry.utils.observeForeverFreshly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoresMainViewModel(app: Application, private val fragmentViewLifecycleOwner: LifecycleOwner /*ignore - just to see that I can*/) : AndroidViewModel(app) {
    @Inject
    lateinit var sp: SharedPreferences

    @Inject
    lateinit var giftCardRepo: GiftCardRepo

    val searchTextMutableLiveData: MutableLiveData<String>
    private lateinit var searchTextMutableLiveDataObserver: Observer<String>
    var maxCardChecked: Boolean
    var corporateCardChecked: Boolean
    var hotCardChecked: Boolean
    private lateinit var storesLiveData: LiveData<List<Store>>

    private val stateMutableLiveData = MutableLiveData<StoreMainState>()
    val stateLiveData: LiveData<StoreMainState> = stateMutableLiveData
    private lateinit var storesLiveDataObserver: Observer<List<Store>>

    init {
        (app as MyApplication).component.inject(this)

        searchTextMutableLiveData = MutableLiveData<String>("")
        maxCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE, true)
        corporateCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE, true)
        hotCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE, true)

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
        filterByPrefix(searchTextMutableLiveData.value!!)
    }

    fun action(intention: StoresMainIntention){
        stateMutableLiveData.value = StoreMainState.Waiting
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is StoresMainIntention.FilterByPrefix -> filterByPrefix(intention.prefix)
                is StoresMainIntention.FilterByCard -> filterByCard(intention.card, intention.isChecked)
                StoresMainIntention.Refresh -> {
//                    delay(500)
                    giftCardRepo.refresh()
                }
                else -> L.e("Unfamiliar intention. Intention = ${intention.javaClass.simpleName}")
            }
        }
    }

    private fun filterByCard(card: GiftCard, isChecked: Boolean) {
        L.i("filterByCard: card= ${card.name}, isChecked= $isChecked")
        sp.edit().putBoolean(getCardSp(card), isChecked).commit()
        when (card) {
            GiftCard.MAX -> maxCardChecked = isChecked
            GiftCard.CORPORATE -> corporateCardChecked = isChecked
            GiftCard.HOT -> hotCardChecked = isChecked
        }

        val filteredStored: List<Store> = getStores().filter { store ->
            shouldStoreBeDisplayed(store, searchTextMutableLiveData.value!!) }
        stateMutableLiveData.postValue(StoreMainState.DisplayData(filteredStored))
    }

    private fun filterByPrefix(prefix: String) {
        val filteredStored: List<Store> = getStores().filter { store -> shouldStoreBeDisplayed(store, prefix) }
        stateMutableLiveData.postValue(StoreMainState.DisplayData(filteredStored))
    }

    private fun shouldStoreBeDisplayed(store: Store, prefix: String): Boolean {
        return if ((store.maxCard && maxCardChecked) ||
            (store.corporateCard && corporateCardChecked) ||
            (store.hotCard && hotCardChecked)) {
            store.storeName.lowercase().startsWith(prefix.lowercase())
        } else {
            false
        }
    }

    private fun getCardSp(card: GiftCard): String = when (card) {
            GiftCard.MAX -> SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE
            GiftCard.CORPORATE -> SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE
            GiftCard.HOT -> SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE
            else -> throw Exception("Unfamiliar GiftCard type!! (${card.name})")
    }

    fun getStores(): List<Store> = storesLiveData.value ?: emptyList()
}

class StoresMainViewModelFactory(
    private val app: Application,
    private val fragmentViewLifecycleOwner: LifecycleOwner) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StoresMainViewModel(app, fragmentViewLifecycleOwner) as T
    }
}