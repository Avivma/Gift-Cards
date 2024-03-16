package com.example.composefirsttry.giftcard.ui.common.dialog.advancedialog

open class CustomDialogAdapterItem (
    open val title: String,
    open val imageUrl: String
) {
    data class Selectable (
        override val title: String,
        override val imageUrl: String,
        var selected: Boolean
    ) : CustomDialogAdapterItem(title, imageUrl)
}
