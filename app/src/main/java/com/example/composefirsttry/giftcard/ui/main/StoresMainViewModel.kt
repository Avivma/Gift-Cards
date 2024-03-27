package com.example.composefirsttry.giftcard.ui.main

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.metadata.repository.MetadataRepo
import com.example.composefirsttry.giftcard.logic.shoppingclubs.model.ShoppingClub
import com.example.composefirsttry.giftcard.logic.shoppingclubs.repository.ShoppingClubsRepo
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.logic.stores.repository.StoresConsiderCardsRepo
import com.example.composefirsttry.giftcard.logic.stores.repository.StoresRepo
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl
import com.example.composefirsttry.giftcard.ui.main.states.StoreMainState
import com.example.composefirsttry.giftcard.ui.main.states.StoresMainIntention
import com.example.composefirsttry.giftcard.ui.utils.SelectedStoresCacheHandler
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.getValidClubs
import com.example.composefirsttry.utils.hasValidClubs
import com.example.composefirsttry.utils.observeForeverFreshly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class StoresMainViewModel @Inject constructor (
    private val storesRepo: StoresRepo,
    private val storesConsiderCardsRepo: StoresConsiderCardsRepo,
    private var shoppingClubsRepo: ShoppingClubsRepo,
    private val metadataRepo: MetadataRepo,
) : ViewModel() {

    private lateinit var storesLiveData: LiveData<List<Store>>
    private lateinit var storesLiveDataObserver: Observer<List<Store>>

    private var stateMutableLiveData = MutableLiveData<StoreMainState>()

    private lateinit var hasMetadataChangedLiveData: LiveData<Boolean>
    private lateinit var hasMetadataChangedLiveDataObserver: Observer<Boolean>

    private lateinit var shoppingClubLiveData: LiveData<List<ShoppingClub>>
    private lateinit var shoppingClubLiveDataObserver: Observer<List<ShoppingClub>>

    private var storesCacheHandler = SelectedStoresCacheHandler()

    init {
        initListeners()
    }

    private fun initListeners() {
        //attach viewModel's stores to db
        storesLiveData = Transformations.map(storesConsiderCardsRepo.getAllStoresCache()) { storesEntities ->
            storesEntities.map { storeEntity ->
                val store = DbToModelConverter.fromEntityToStore(storeEntity)
                storesCacheHandler.updateStoreWithCacheProperties(store)
                store
            }
        }
        //notify when changes happens
        storesLiveDataObserver = storesLiveData.observeForeverFreshly(Observer { ignore ->
            sendFreshData()
        })

        //attach viewModel's shoppingClubs to db
        shoppingClubLiveData = Transformations.map(shoppingClubsRepo.getAllExistingShoppingClubs()) { clubEntities ->
            clubEntities.map { clubEntity ->
                val shoppingClub = DbToModelConverter.fromEntityToShoppingClub(clubEntity)
                shoppingClub
            }
        }
        //notify when changes happens
        shoppingClubLiveDataObserver = shoppingClubLiveData.observeForeverFreshly(Observer { ignore ->
            sendFreshData()
        })

        //attach viewModel's hasMetadataChanged to db
        hasMetadataChangedLiveData = metadataRepo.serverDataChangedLiveData
        //notify when changes happens
        hasMetadataChangedLiveDataObserver = hasMetadataChangedLiveData.observeForeverFreshly(Observer { dataChanged ->
            if (dataChanged) {
                displayForceInitializeDialog()
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        storesLiveData.removeObserver(storesLiveDataObserver)
        shoppingClubLiveData.removeObserver(shoppingClubLiveDataObserver)
        hasMetadataChangedLiveData.removeObserver(hasMetadataChangedLiveDataObserver)
    }

    //NOTE: Use this way, because LiveData stores events and triggers them once new LifecycleOwner is observe to them.
    // ViewModel doesn't create new Livedata on backpress, but the fragment has new LifecycleOwner - this cause UX bug.
    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<StoreMainState>) {
        stateMutableLiveData = MutableLiveData<StoreMainState>()
        stateMutableLiveData.observe(owner, observer)
    }

    private fun sendFreshData() {
        L.i("sendFreshData")
        sendStateDisplayData()
    }

    private fun displayForceInitializeDialog() {
        L.i("displayForceInitializeDialog")
        stateMutableLiveData.postValue(StoreMainState.DisplayForceInitializeDialog)
    }

    fun action(intention: StoresMainIntention){
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is StoresMainIntention.FilterByPrefix -> filterByPrefix(intention.prefix)
                StoresMainIntention.ClearSearchBox -> clearSearchBox()
                StoresMainIntention.Refresh -> {
                    stateMutableLiveData.postValue(StoreMainState.Waiting)
                    if (isFirstTimeDataFetched()) {
                        refresh()
                    } else {
                        filterByPrefix(searchTextValue_static)
                    }
                }
                StoresMainIntention.FilterBySelectedStores -> filterBySelectedStores()
                StoresMainIntention.ClearStoresSelection -> clearStoresSelection()
                is StoresMainIntention.SelectStore -> storeSelected(intention.store)
                is StoresMainIntention.OpenStoreDialog -> openStoreDialog(intention.store)
                is StoresMainIntention.AddStoreToFavorites -> addStoreToFavorites(intention.store)
                is StoresMainIntention.NavigateToCardsScreen -> navigateToCardsScreen(intention.shoppingClubId)
                is StoresMainIntention.ShoppingClubChecked -> shoppingClubChecked(intention.clubDialogAdapterItem)
                StoresMainIntention.AddSeparationMarkToSearch -> addSeparationMarkToSearch()
                StoresMainIntention.Initialize -> initializingApp()
                StoresMainIntention.OpenCardsSelectionDialog -> openCardsSelectionDialog()
                else -> L.e("Unfamiliar intention. Intention = ${intention.javaClass.simpleName}")
            }
        }
    }

    private fun openCardsSelectionDialog() {
        val dialogAdapterItems = getShoppingClubs().map { shoppingClub -> CustomDialogAdapterItem.Selectable(shoppingClub.clubId, shoppingClub.type, shoppingClub.imageUrl, shoppingClub.checked) }
        stateMutableLiveData.postValue(StoreMainState.CardsDialogOpened(dialogAdapterItems))
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            launch { metadataRepo.refresh() }
            launch { storesRepo.refresh() }
        }
    }

    private fun initializingApp() {
        metadataRepo.clear()
        stateMutableLiveData.postValue(StoreMainState.Navigation.NavigateToInitializeScreen)
    }

    private fun addSeparationMarkToSearch() {
        searchTextValue_static += " || " //separation mark = "||"
        stateMutableLiveData.postValue(StoreMainState.SearchBoxTextChanged(searchTextValue_static, searchTextValue_static.length))
    }

    private fun clearSearchBox() {
        searchTextValue_static = ""
        stateMutableLiveData.postValue(StoreMainState.SearchBoxTextChanged("", 0))
    }

    private fun shoppingClubChecked(clubDialogAdapterItem: CustomDialogAdapterItem.Selectable) {
        shoppingClubsRepo.updateShoppingClubChecked(clubDialogAdapterItem.id, clubDialogAdapterItem.selected)
    }

    private fun navigateToCardsScreen(shoppingClubId: String) {
        stateMutableLiveData.postValue(StoreMainState.Navigation.NavigateToCardsScreen(shoppingClubId))
    }

    private fun addStoreToFavorites(store: Store) {
        storesRepo.addStoreToFavorites(DbToModelConverter.fromStoreToEntity(store))
    }

    private fun openStoreDialog(store: Store) {
        stateMutableLiveData.postValue(StoreMainState.StoreDialogOpened(store))
    }

    private fun clearStoresSelection() {
        L.i("clearStoresSelection: storesHasBeenSelected= ${storesCacheHandler.storesHasBeenSelected}")
        storesCacheHandler.clearStoresSelection()
        if (storesCacheHandler.storesHasBeenSelected == SelectedStoresCacheHandler.VISIBLE) //deactivate
            sendStateDisplayData()
        else { //make invisible
            getStores().forEach { storesCacheHandler.updateStoreWithCacheProperties(it) }
            sendStateDisplayData(hideStoreSelectionFilter = true)
        }
    }

    private fun storeSelected(store: Store) {
        store.selected = getStoreNewState(store)
        storesCacheHandler.updateCache(store)

        when (storesCacheHandler.storesHasBeenSelected) {
            SelectedStoresCacheHandler.VISIBLE, SelectedStoresCacheHandler.ACTIVE -> stateMutableLiveData.postValue(StoreMainState.StoreSelected(store, true))
            SelectedStoresCacheHandler.INVISIBLE -> sendStateDisplayData(hideStoreSelectionFilter = true)
        }
    }

    private fun getStoreNewState(store: Store): Boolean = !store.selected //get reverse store selection state

    private fun getFilteredStores(): List<Store> = getStores().filter { store -> shouldStoreBeDisplayed(store, searchTextValue_static) }

    private fun filterBySelectedStores() {
        L.i("filterBySelectedStores")
        storesCacheHandler.setStoresSelection(SelectedStoresCacheHandler.ACTIVE)
        sendStateDisplayData()
    }

    private fun filterByPrefix(prefix: String) {
        searchTextValue_static = prefix
        sendStateDisplayData(searchIconVisible = prefix.isEmpty())
    }

    private fun sendStateDisplayData(hideStoreSelectionFilter: Boolean = false, searchIconVisible: Boolean = true) {
        val shoppingClubs = getShoppingClubs()
        val shoppingClubsAmount = shoppingClubs.size
        val shoppingClubsChecked = shoppingClubs.filter { club -> club.checked }.size
        val allCardsChecked = shoppingClubsAmount == shoppingClubsChecked
        val cardsTextModel = StoreMainTextModel(shoppingClubsChecked, shoppingClubsAmount, allCardsChecked)
        stateMutableLiveData.postValue(StoreMainState.DisplayData(getDisplayedData(), cardsTextModel = cardsTextModel, hideStoreSelectionFilter = hideStoreSelectionFilter, searchIconVisible = searchIconVisible))
    }

    /**
     * FilteredStores and AvailableClubs
     */
    private fun getDisplayedData(): Map<Store, List<ClubIdAndUrl>> {
        val filteredStores: List<Store> = getFilteredStores()
        val storesAndClubs = mapAvailableClubs(filteredStores)
        return storesAndClubs
    }

    private fun mapAvailableClubs(filteredStores: List<Store>): Map<Store, List<ClubIdAndUrl>> {
        return filteredStores.associateWith { store ->
            return@associateWith store.getValidClubs(getCheckedShoppingClubs())
                .map { club -> ClubIdAndUrl(club.clubId, club.imageUrl) }
        }
    }

    private fun shouldStoreBeDisplayed(store: Store, prefix: String): Boolean {
        val storeRelatedCardExist = store.hasValidClubs(getCheckedShoppingClubs())
        return storeRelatedCardExist
                && storesCacheHandler.hasStoreIncludedInCache(store)
                && doesStoreNameStartWithPrefix(store.storeName.lowercase(), prefix.lowercase())
    }

    private fun doesStoreNameStartWithPrefix(storeName: String, complexPrefix: String): Boolean {
        val acceptablePrefixes: List<String> = complexPrefix.split("||").map { it.trim() }
        return acceptablePrefixes.any { storeName.startsWith(it) }
    }

    private fun getStores(): List<Store> = storesLiveData.value ?: emptyList()

    private fun getShoppingClubs(): List<ShoppingClub> = shoppingClubLiveData.value ?: emptyList()
    private fun getCheckedShoppingClubs(): List<ShoppingClub> = shoppingClubLiveData.value?.filter { it.checked } ?: emptyList()

    private fun isFirstTimeDataFetched(): Boolean = firstTimeFetchData.getAndSet(false)

    companion object {
        private var firstTimeFetchData: AtomicBoolean = AtomicBoolean(true)
        var searchTextValue_static: String = ""
    }
}

/* //Just for learning
class StoresMainViewModelFactory(
    private val fragmentViewLifecycleOwner: LifecycleOwner) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StoresMainViewModel(fragmentViewLifecycleOwner) as T
    }
}*/
