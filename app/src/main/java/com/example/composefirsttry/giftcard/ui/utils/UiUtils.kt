package com.example.composefirsttry.giftcard.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.example.composefirsttry.R

object UiUtils {
    fun getRemoveCardDialog(context: Context, removeListener: DialogInterface.OnClickListener): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(R.string.cards_remove_card_dialog_title)
            .setNeutralButton(R.string.cards_dialog_remove_button_text, removeListener)
            .setNegativeButton(R.string.cards_dialog_cancel_button_text) { dialog, _ -> dialog.dismiss() }
            .create()
    }
}