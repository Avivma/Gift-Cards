package com.example.composefirsttry.giftcard.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.MyApplication
import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.giftcard.model.Store
import com.example.composefirsttry.giftcard.repository.GiftCardRepo
import com.example.composefirsttry.giftcard.ui.states.StoreMainState
import com.example.composefirsttry.giftcard.ui.states.StoresMainIntention
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.SPKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StoresMainViewModel(app: Application, private val fragmentViewLifecycleOwner: LifecycleOwner) : AndroidViewModel(app) {
    @Inject
    lateinit var sp: SharedPreferences

    val searchTextMutableLiveData: MutableLiveData<String>
    var maxCardChecked: Boolean
    var corporateCardChecked: Boolean
    var hotCardChecked: Boolean
    private lateinit var storesLiveData: LiveData<List<Store>>

    private val stateMutableLiveData = MutableLiveData<StoreMainState>()
    val stateLiveData: LiveData<StoreMainState> = stateMutableLiveData

    private var hasDbInitialized = false
    init {
        (app as MyApplication).component.inject(this)

        searchTextMutableLiveData = MutableLiveData<String>("")
        maxCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE, true)
        corporateCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE, true)
        hotCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE, true)

        viewModelScope.launch(Dispatchers.IO) {
            GiftCardRepo.initializeDB(app)
            withContext(Dispatchers.Main) {
                initLiveData()
                observeForDbChanges()
                hasDbInitialized = true
            }
        }
    }

    @MainThread
    private fun initLiveData() {
        searchTextMutableLiveData.value = ""
        //attach viewModel's stores to db
        storesLiveData = Transformations.map(GiftCardRepo.getAllStoresDb()) { storesEntities ->
            storesEntities.map { storeEntity ->
                DbToModelConverter.fromEntityToStore(storeEntity)
            }
        }
    }

    private var firstTimeData = true

    @MainThread
    private fun observeForDbChanges() {
        firstTimeData = true
        //notify when changes happens
        storesLiveData.removeObservers(fragmentViewLifecycleOwner)
        storesLiveData.observe(fragmentViewLifecycleOwner, Observer { ignore ->
            //send fresh data to fragment
            sendFreshData()
        })
    }

    private fun sendFreshData() {
        //try to fix sending last data before observing. Helpful link: https://stackoverflow.com/questions/49832787/livedata-prevent-receive-the-last-value-when-start-observing
        if (firstTimeData)
            firstTimeData = false
        else
            filterByPrefix(searchTextMutableLiveData.value!!)
    }

    fun action(intention: StoresMainIntention){
        stateMutableLiveData.value = StoreMainState.Waiting
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is StoresMainIntention.FilterByPrefix -> filterByPrefix(intention.prefix)
                is StoresMainIntention.FilterByCard -> filterByCard(intention.card, intention.isChecked)
                StoresMainIntention.Refresh -> GiftCardRepo.refresh()
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

    fun getStores(): List<Store> = if (hasDbInitialized) storesLiveData.value ?: emptyList() else emptyList()
}

class StoresMainViewModelFactory(
    private val app: Application,
    private val fragmentViewLifecycleOwner: LifecycleOwner) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StoresMainViewModel(app, fragmentViewLifecycleOwner) as T
    }
}