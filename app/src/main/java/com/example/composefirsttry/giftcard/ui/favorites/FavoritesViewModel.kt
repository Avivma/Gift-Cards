package com.example.composefirsttry.giftcard.ui.favorites

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.shoppingclubs.model.ShoppingClub
import com.example.composefirsttry.giftcard.logic.shoppingclubs.repository.ShoppingClubsRepo
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.logic.stores.repository.StoresConsiderCardsRepo
import com.example.composefirsttry.giftcard.logic.stores.repository.StoresRepo
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl
import com.example.composefirsttry.giftcard.ui.favorites.states.FavoritesIntention
import com.example.composefirsttry.giftcard.ui.favorites.states.FavoritesState
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.getValidClubs
import com.example.composefirsttry.utils.observeForeverFreshly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor (
    private val storesConsiderCardsRepo: StoresConsiderCardsRepo,
    private val storesRepo: StoresRepo,
    private var shoppingClubsRepo: ShoppingClubsRepo
    ) : ViewModel() {

    private var stateMutableLiveData = MutableLiveData<FavoritesState>()

    private lateinit var storesLiveData: LiveData<List<Store>>
    private lateinit var storesLiveDataObserver: Observer<List<Store>>

    private lateinit var shoppingClubLiveData: LiveData<List<ShoppingClub>>
    private lateinit var shoppingClubLiveDataObserver: Observer<List<ShoppingClub>>

    init {
        initListeners()
    }

    private fun initListeners() {
        //attach viewModel's stores to db
        storesLiveData = storesConsiderCardsRepo.getAllStoresCache().map { storesEntities ->
            storesEntities.map { storeEntity ->
                DbToModelConverter.fromEntityToStore(storeEntity)
            }
        }
        //notify when changes happens
        storesLiveDataObserver = storesLiveData.observeForeverFreshly(Observer { ignore ->
            sendFreshData()
        })

        //attach viewModel's shoppingClubs to db
        shoppingClubLiveData = shoppingClubsRepo.getAllExistingShoppingClubs().map { clubEntities ->
            clubEntities.map { clubEntity ->
                val shoppingClub = DbToModelConverter.fromEntityToShoppingClub(clubEntity)
                shoppingClub
            }
        }
        //notify when changes happens
        shoppingClubLiveDataObserver = shoppingClubLiveData.observeForeverFreshly(Observer { ignore ->
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
                    sendStateDisplayData()
                }
                is FavoritesIntention.OpenStoreDialog -> openStoreDialog(intention.store)
                is FavoritesIntention.RemoveStore -> removeStoreFromFavorites(intention.store)
                is FavoritesIntention.RemoveAllStores -> removeAllStoresFromFavorites()
                is FavoritesIntention.OpenRemoveAllDialog -> removeAllDialog()
                is FavoritesIntention.NavigateToCardsScreen -> retrieveDataForNavigationToCardsScreen(intention.shoppingClubId)
                else -> throw Exception("Unfamiliar intention. Intention = ${intention.javaClass.simpleName}")
            }
        }
    }

    private fun retrieveDataForNavigationToCardsScreen(shoppingClubId: String) {
        stateMutableLiveData.postValue(FavoritesState.Navigation.NavigateToCardsScreen(shoppingClubId))
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
        sendStateDisplayData()
    }

    private fun sendStateDisplayData() {
        val displayData = getDisplayedData()
        stateMutableLiveData.postValue(FavoritesState.DisplayData(displayData, shouldShowDeleteAll(displayData)))
    }

    private fun getDisplayedData(): Map<Store, List<ClubIdAndUrl>> = mapAvailableClubs(getStores())

    private fun mapAvailableClubs(filteredStores: List<Store>): Map<Store, List<ClubIdAndUrl>> {
        return filteredStores.associateWith { store ->
            return@associateWith store.getValidClubs(getShoppingClubs())
                .map { club -> ClubIdAndUrl(club.clubId, club.imageUrl) }
        }
    }

    private fun shouldShowDeleteAll(data: Map<Store, List<ClubIdAndUrl>>): Boolean = data.isNotEmpty()

    private fun getStores(): List<Store> = storesLiveData.value?.filter { it.favorite } ?: emptyList()

    private fun getShoppingClubs(): List<ShoppingClub> = shoppingClubLiveData.value ?: emptyList()

    companion object {
        val CLASS_NAME: String = FavoritesViewModel::class.java.simpleName
    }
}