package com.example.composefirsttry.giftcard.logic.cardsmetadata.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composefirsttry.giftcard.common.fetchimages.GlideHandler
import com.example.composefirsttry.giftcard.logic.cardsmetadata.db.MetadataCardsDbHandler
import com.example.composefirsttry.giftcard.logic.cardsmetadata.network.RestMetadataCardService
import com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet.SheetItem
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataCardsRepo @Inject constructor(
    private var restService: RestMetadataCardService,
    private var metadataDbHandler: MetadataCardsDbHandler,
    private val glideHandler: GlideHandler
) {
    private val initFinishedMutableLiveData = MutableLiveData<Boolean>(false)
    val initFinishedLiveData: LiveData<Boolean> = initFinishedMutableLiveData

    private val serverDataChangedMutableLiveData = MutableLiveData<Boolean>(false)
    val serverDataChangedLiveData: LiveData<Boolean> = serverDataChangedMutableLiveData

    @WorkerThread
    fun getMetadataCardsDb(): List<SheetItem> = metadataDbHandler.get().items

    @WorkerThread
    suspend fun initializing() {
        withContext(Dispatchers.IO) {
            val metadataListServer: List<SheetItem> = restService.getCardsMetadata()
            metadataDbHandler.saveData(metadataListServer)

            val preloadJobs = metadataDbHandler.get().items.map { metadataCard ->
                return@map async {
                    downloadImage(metadataCard.imageUrl)
                }
            }

            preloadJobs.awaitAll()
            initFinishedMutableLiveData.postValue(true)
        }
    }

    fun isMetadataExist(): Boolean = metadataDbHandler.isMetadataExist()

    @WorkerThread
    fun refresh() {
        checkDataFromServer()
    }

    @WorkerThread
    fun clear() {
        metadataDbHandler.clear()
    }

    /*private*/ suspend fun downloadImage(imageUrl: String) { //private suspend method aren't debuggable (it is a bug in coroutine)
        glideHandler.downloadImage(imageUrl)
    }

    private fun checkDataFromServer() {
        val metadataListServer: List<SheetItem> = restService.getCardsMetadata()
        if (!metadataDbHandler.isSameData(metadataListServer)) {
            serverDataChangedMutableLiveData.postValue(true)
        }
    }
}