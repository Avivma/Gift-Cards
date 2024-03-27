package com.example.composefirsttry.giftcard.logic.metadata.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composefirsttry.giftcard.common.fetchimages.GlideHandler
import com.example.composefirsttry.giftcard.logic.metadata.MetadataDbHelper
import com.example.composefirsttry.giftcard.logic.metadata.network.RestMetadataService
import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetData
import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetItem
import com.example.composefirsttry.giftcard.logic.shoppingclubs.repository.ShoppingClubsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataRepo @Inject constructor(
    private var restService: RestMetadataService,
    private var metadataDbHelper: MetadataDbHelper,
    private val glideHandler: GlideHandler,
    private val shoppingClubsRepo: ShoppingClubsRepo
) {
    private val initFinishedMutableLiveData = MutableLiveData<Boolean>(false)
    val initFinishedLiveData: LiveData<Boolean> = initFinishedMutableLiveData

    private val serverDataChangedMutableLiveData = MutableLiveData<Boolean>(false)
    val serverDataChangedLiveData: LiveData<Boolean> = serverDataChangedMutableLiveData

    fun isMetadataExist(): Boolean = metadataDbHelper.isMetadataExist()

    @WorkerThread
    suspend fun initializing() {
        withContext(Dispatchers.IO) {
            val metadataServer: SheetData = restService.getCardsMetadata()

            val deferDbJob = async {
                initializeDb(metadataServer.cardItems)
                metadataDbHelper.saveMetadata(metadataServer)
            }
            val deferDownloadJobs = metadataServer.cardItems.map { metadataCard ->
                return@map async {
                    downloadImage(metadataCard.imageUrl)
                }
            }
            val preloadJobs = listOf(deferDbJob) + deferDownloadJobs

            preloadJobs.awaitAll()
            initFinishedMutableLiveData.postValue(true)
        }
    }

    private fun initializeDb(metadataCardListServer: List<SheetItem>) {
        shoppingClubsRepo.initializeDb(metadataCardListServer)
    }

    @WorkerThread
    fun refresh() {
        checkDataFromServer()
    }

    @WorkerThread
    fun clear() {
        metadataDbHelper.clear()
    }

    /*private*/ suspend fun downloadImage(imageUrl: String) { //private suspend method aren't debuggable (it is a bug in coroutine)
        glideHandler.downloadImage(imageUrl)
    }

    private fun checkDataFromServer() {
        val metadataServer: SheetData = restService.getCardsMetadata()
        if (!metadataDbHelper.isSameData(metadataServer)) {
            serverDataChangedMutableLiveData.postValue(true)
        }
    }
}