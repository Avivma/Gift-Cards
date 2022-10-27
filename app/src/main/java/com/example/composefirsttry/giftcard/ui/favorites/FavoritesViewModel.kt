package com.example.composefirsttry.giftcard.ui.favorites

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.logic.stores.repository.StoresRepo
import com.example.composefirsttry.giftcard.ui.favorites.states.FavoritesIntention
import com.example.composefirsttry.giftcard.ui.favorites.states.FavoritesState
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.observeForeverFreshly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor (
    private val storesRepo: StoresRepo
) : ViewModel() {

    private var stateMutableLiveData = MutableLiveData<FavoritesState>()

    private lateinit var storesLiveData: LiveData<List<Store>>
    private lateinit var storesLiveDataObserver: Observer<List<Store>>

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
    }

    override fun onCleared() {
        super.onCleared()
        storesLiveData.removeObserver(storesLiveDataObserver)
    }

    //NOTE: Use this way, because LiveData stores events and triggers them once new LifecycleOwner is observe to them.
    // ViewModel doesn't create new Livedata on backpress, but the fragment has new LifecycleOwner - this cause UX bug.
    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<FavoritesState>) {
        stateMutableLiveData = MutableLiveData<FavoritesState>()
        stateMutableLiveData.observe(owner, observer)
    }

    fun action(intention: FavoritesIntention){
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                is FavoritesIntention.Refresh -> {
                    stateMutableLiveData.postValue(FavoritesState.Waiting)
                    stateMutableLiveData.postValue(FavoritesState.DisplayData(getStores()))
                }
                is FavoritesIntention.OpenStoreDialog -> openStoreDialog(intention.store)
                is FavoritesIntention.RemoveStore -> removeStoreFromFavorites(intention.store)
                is FavoritesIntention.RemoveAllStores -> removeAllStoresFromFavorites()
                is FavoritesIntention.OpenRemoveAllDialog -> removeAllDialog()
                is FavoritesIntention.NavigateToCardsScreen -> retrieveDataForNavigationToCardsScreen(intention.giftCardType)
                else -> L.e("Unfamiliar intention. Intention = ${intention.javaClass.simpleName}")
            }
        }
    }

    private fun retrieveDataForNavigationToCardsScreen(giftCardType: GiftCardType) {
        stateMutableLiveData.postValue(FavoritesState.Navigation.NavigateToCardsScreen(giftCardType))
    }

    private fun removeAllDialog() {
        stateMutableLiveData.postValue(FavoritesState.RemoveAllDialogOpened)
    }

    private fun openStoreDialog(store: Store) {
        stateMutableLiveData.postValue(FavoritesState.StoreDialogOpened(store))
    }

    private fun removeStoreFromFavorites(store: Store) {
        storesRepo.removeStoreFromFavorites(store.storeName)
    }

    private fun removeAllStoresFromFavorites() {
        storesRepo.resetFavorites()
    }

    private fun sendFreshData() {
        L.i("$CLASS_NAME - sendFreshData")
        stateMutableLiveData.postValue(FavoritesState.DisplayData(getStores()))
    }

    private fun getStores(): List<Store> = storesLiveData.value?.filter { it.favorite } ?: emptyList()

    companion object {
        val CLASS_NAME: String = FavoritesViewModel::class.java.simpleName
    }
}