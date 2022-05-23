package com.example.composefirsttry.amazon

enum class AmazonSku(val sku: String, val availableMarketplace: String) {
    MY_MAGAZINE_SUBS("com.amazon.sample.iap.subscription.mymagazine", "US");

    companion object {
        fun fromSku(sku: String, marketplace: String?): AmazonSku? {
            return if (MY_MAGAZINE_SUBS.sku == sku && (null == marketplace || (MY_MAGAZINE_SUBS.availableMarketplace == marketplace))) {
                MY_MAGAZINE_SUBS
            }
            else null
        }
    }
}