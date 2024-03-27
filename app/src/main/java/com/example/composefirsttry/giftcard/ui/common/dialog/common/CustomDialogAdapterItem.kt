package com.example.composefirsttry.giftcard.ui.common.dialog.common

open class CustomDialogAdapterItem (
    open val id: String,
    open val title: String,
    open val imageUrl: String
) {
    data class Selectable (
        override val id: String,
        override val title: String,
        override val imageUrl: String,
        var selected: Boolean
    ) : CustomDialogAdapterItem(id, title, imageUrl)
}
