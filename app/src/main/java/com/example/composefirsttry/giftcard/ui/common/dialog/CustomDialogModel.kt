package com.example.composefirsttry.giftcard.ui.common.dialog

import android.view.View
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem

class CustomDialogModel {
    var title: String = ""

    var message: String = ""
    var messageVisible: Boolean = false

    var positiveButtonText: String = ""
    var positiveButtonVisible: Boolean = false
    var positiveButtonClickListener: View.OnClickListener? = null

    var negativeButtonText: String = ""
    var negativeButtonVisible: Boolean = false
    var negativeButtonClickListener: View.OnClickListener? = null

    var itemsVisible: Boolean = false
    var items: List<CustomDialogAdapterItem> = listOf()
    var itemsListener: ((CustomDialogAdapterItem) -> Unit)? = null
}