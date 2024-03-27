package com.example.composefirsttry.giftcard.ui.initializing

import androidx.lifecycle.*
import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.logic.metadata.repository.MetadataRepo
import com.example.composefirsttry.giftcard.ui.initializing.state.InitializeIntention
import com.example.composefirsttry.giftcard.ui.initializing.state.InitializeState
import com.example.composefirsttry.utils.observeForeverFreshly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitializeViewModel @Inject constructor (
    private val metadataRepo: MetadataRepo,
    private val cardsRepo: CardsRepo,
) : ViewModel() {

    private lateinit var hasMetadataFinishedLiveData: LiveData<Boolean>
    private lateinit var hasMetadataFinishedLiveDataObserver: Observer<Boolean>

    private var stateMutableLiveData = MutableLiveData<InitializeState>()

    init {
        initListeners()
    }

    private fun initListeners() {
        //attach viewModel's stores to db
        hasMetadataFinishedLiveData = metadataRepo.initFinishedLiveData
        //notify when changes happens
        hasMetadataFinishedLiveDataObserver = hasMetadataFinishedLiveData.observeForeverFreshly(Observer { ignore ->
            navigateToDestination()
        })
    }

    override fun onCleared() {
        super.onCleared()
        hasMetadataFinishedLiveData.removeObserver(hasMetadataFinishedLiveDataObserver)
    }


    //NOTE: Use this way, because LiveData stores events and triggers them once new LifecycleOwner is observe to them.
    // ViewModel doesn't create new Livedata on backpress, but the fragment has new LifecycleOwner - this cause UX bug.
    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<InitializeState>) {
        stateMutableLiveData = MutableLiveData<InitializeState>()
        stateMutableLiveData.observe(owner, observer)
    }

    private fun navigateToDestination() {
        L.i("navigateToDestination")
        if (shouldNavigateToLandingScreen()) {
            stateMutableLiveData.postValue(InitializeState.Navigation.NavigateToLandingScreen)
        } else {
            stateMutableLiveData.postValue(InitializeState.Navigation.NavigateToMainScreen)
        }
    }

    fun action(intention: InitializeIntention){
        viewModelScope.launch(Dispatchers.IO) {
            when (intention) {
                InitializeIntention.Initializing -> initializing()
                else -> L.e("Unfamiliar intention. Intention = ${intention.javaClass.simpleName}")
            }
        }
    }

    private fun initializing() {
        viewModelScope.launch(Dispatchers.IO) {
            metadataRepo.initializing()
        }
    }

    private fun shouldNavigateToLandingScreen(): Boolean = !cardsRepo.hasAnyCard()
}