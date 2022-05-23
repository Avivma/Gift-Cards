package com.example.composefirsttry.amazon

import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.model.ProductDataResponse
import com.amazon.device.iap.model.PurchaseResponse
import com.amazon.device.iap.model.PurchaseUpdatesResponse
import com.amazon.device.iap.model.UserDataResponse

object AmazonIapPurchasingListener : PurchasingListener {
    override fun onUserDataResponse(userDataResponse: UserDataResponse?) {
        TODO("Not yet implemented")
    }

    override fun onProductDataResponse(productDataResponse: ProductDataResponse?) {
        TODO("Not yet implemented")
    }

    override fun onPurchaseResponse(purchaseResponse: PurchaseResponse?) {
        TODO("Not yet implemented")
    }

    override fun onPurchaseUpdatesResponse(purchaseUpdatesResponse: PurchaseUpdatesResponse?) {
        TODO("Not yet implemented")
    }
}