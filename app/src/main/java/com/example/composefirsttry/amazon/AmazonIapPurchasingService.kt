package com.example.composefirsttry.amazon

import android.content.Context
import androidx.annotation.WorkerThread
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.ProductDataResponse
import com.amazon.device.iap.model.PurchaseResponse
import com.amazon.device.iap.model.PurchaseUpdatesResponse
import com.amazon.device.iap.model.UserDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AmazonIapPurchasingService: PurchasingListener {
    private lateinit var continuationUserDataResponse: Continuation<UserDataResponse>
    private lateinit var continuationProductDataResponse: Continuation<ProductDataResponse>
    private lateinit var continuationPurchaseResponse: Continuation<PurchaseResponse>
    private lateinit var continuationPurchaseUpdatesResponse: Continuation<PurchaseUpdatesResponse>

    fun init(appContext: Context) {
        PurchasingService.registerListener(appContext, this)
    }

    suspend fun getUserData(): UserDataResponse {
        return withContext(Dispatchers.IO) {
            return@withContext suspendCoroutine<UserDataResponse> { continuation ->
                continuationUserDataResponse = continuation
                PurchasingService.getUserData()
            }
        }
    }

    @WorkerThread
    suspend fun purchase(sku: String): PurchaseResponse {
        return suspendCoroutine { continuation ->
            continuationPurchaseResponse = continuation
            PurchasingService.purchase(sku)
        }
    }

    override fun onUserDataResponse(userDataResponse: UserDataResponse?) {
        if (userDataResponse != null) continuationUserDataResponse.resume(userDataResponse)
        else continuationUserDataResponse.resumeWithException(Exception("Error: userDataResponse is null"))
    }

    override fun onProductDataResponse(productDataResponse: ProductDataResponse?) {
        if (productDataResponse != null) continuationProductDataResponse.resume(productDataResponse)
        else continuationProductDataResponse.resumeWithException(Exception("Error: productDataResponse is null"))
    }

    override fun onPurchaseResponse(purchaseResponse: PurchaseResponse?) {
        if (purchaseResponse != null) continuationPurchaseResponse.resume(purchaseResponse)
        else continuationPurchaseResponse.resumeWithException(Exception("Error: purchaseResponse is null"))
    }

    override fun onPurchaseUpdatesResponse(purchaseUpdatesResponse: PurchaseUpdatesResponse?) {
        if (purchaseUpdatesResponse != null) continuationPurchaseUpdatesResponse.resume(purchaseUpdatesResponse)
        else continuationPurchaseUpdatesResponse.resumeWithException(Exception("Error: purchaseUpdatesResponse is null"))
    }

}