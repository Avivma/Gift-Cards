package com.example.composefirsttry.giftcard.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.composefirsttry.L
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigateOutsideHandler @Inject constructor(
    private val context: Context
){
    fun launchApplication(applicationId: String) {
        var launchIntent = context.packageManager.getLaunchIntentForPackage(applicationId)
        L.i("launchMaxApplication: launchIntent = ${if (launchIntent != null) "valid" else "null"} (applicationId = $applicationId)")
        if (launchIntent == null) {
            launchIntent = Intent(Intent.ACTION_VIEW)
            launchIntent.data = Uri.parse("market://details?id=$applicationId")
        }
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    }

    fun launchSite(siteAddress: String) {
        L.i("launchSite: go to site = $siteAddress")
        val launchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(siteAddress))
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    }

    companion object {
        const val MAX_APPLICATION_ID = "com.ideomobile.leumicard"
        const val ISRACARD_SITE_ADDRESS = "https://service.isracard.co.il/isracard/externals?reqName=GiftCardCharging_934"
        const val TAV_HAHAM_APPLICATION_ID = "com.hot.benefits"
    }
}