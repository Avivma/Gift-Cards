package com.example.composefirsttry.amazon

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.FulfillmentResult
import com.amazon.device.iap.model.PurchaseResponse
import com.amazon.device.iap.model.Receipt
import com.amazon.device.iap.model.UserData
import com.example.composefirsttry.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.HashSet
import javax.inject.Inject

class AmazonIapManager @Inject constructor(
    var context: Context,
    var amazonIapPurchasingService: AmazonIapPurchasingService) {
    fun init() {
        amazonIapPurchasingService.init(context)
    }

    suspend fun purchase(sku: String) {
        withContext(Dispatchers.IO) {
            val purchaseResponse = amazonIapPurchasingService.purchase(sku)
            val requestId = purchaseResponse.requestId.toString()
            val userId = purchaseResponse.userData.userId
            val status = purchaseResponse.requestStatus
            L.i("onPurchaseResponse: requestId ($requestId) userId ($userId) purchaseRequestStatus ($status)")
            when (status) {
//                PurchaseResponse.RequestStatus.SUCCESSFUL -> {
//                    val receipt = purchaseResponse.receipt
//                    L.i("onPurchaseResponse: receipt json: ${receipt.toJSON()}")
//                    handleSubscriptionPurchase(receipt, purchaseResponse.userData)
//                }
//                PurchaseResponse.RequestStatus.ALREADY_PURCHASED ->
//                    L.i("onPurchaseResponse: already purchased, you should verify the subscription purchase on your side and make sure the purchase was granted to customer")
//                PurchaseResponse.RequestStatus.INVALID_SKU -> {
//                    L.i("onPurchaseResponse: invalid SKU!  onProductDataResponse should have disabled buy button already.")
//                    val unavailableSkus: MutableSet<String> = HashSet()
//                    unavailableSkus.add(purchaseResponse.receipt.sku)
//                    iapManager.disablePurchaseForSkus(unavailableSkus)
//                }
//                PurchaseResponse.RequestStatus.FAILED, PurchaseResponse.RequestStatus.NOT_SUPPORTED -> {
//                    L.i("onPurchaseResponse: failed so remove purchase request from local storage")
//                    iapManager.purchaseFailed(purchaseResponse.receipt.sku)
//                }
            }
        }
    }

    private fun handleSubscriptionPurchase(receipt: Receipt, userData: UserData) {
        try {
            if (receipt.isCanceled) {
                // Check whether this receipt is for an expired or canceled
                // subscription
//                revokeSubscription(receipt, userData.userId)
            } else {
                // We strongly recommend that you verify the receipt on
                // server-side.
                if (!verifyReceiptFromYourService(receipt.receiptId, userData)) {
                    // if the purchase cannot be verified,
                    // show relevant error message to the customer.
//                    mainActivity.showMessage("Purchase cannot be verified, please retry later.")
//                    return
                }
                grantSubscriptionPurchase(receipt, userData)
            }
            return
        } catch (e: Throwable) {
//            mainActivity.showMessage("Purchase cannot be completed, please retry")
        }
    }

    private fun verifyReceiptFromYourService(receiptId: String, userData: UserData): Boolean {
        // TODO Add your own server side accessing and verification code
        return true
    }

    private fun grantSubscriptionPurchase(receipt: Receipt, userData: UserData) {
//        val mySku: MySku = MySku.fromSku(receipt.sku, userIapData.getAmazonMarketplace())
//        // Verify that the SKU is still applicable.
//        if (mySku !== MySku.MY_MAGAZINE_SUBS) {
//            L.i("The SKU [${receipt.sku}] in the receipt is not valid anymore")
//            // if the sku is not applicable anymore, call
//            // PurchasingService.notifyFulfillment with status "UNAVAILABLE"
//            PurchasingService.notifyFulfillment(receipt.receiptId, FulfillmentResult.UNAVAILABLE)
//            return
//        }
//        try {
//            // Set the purchase status to fulfilled for your application
//            PurchasingService.notifyFulfillment(receipt.receiptId, FulfillmentResult.FULFILLED)
//            ///////////////////////////////////
//            // start Cumulus activation process
//            ///////////////////////////////////
//        } catch (e: Throwable) {
//            // If for any reason the app is not able to fulfill the purchase,
//            // add your own error handling code here.
//            L.e("Failed to grant entitlement purchase, with error: ", e)
//        }
    }

}