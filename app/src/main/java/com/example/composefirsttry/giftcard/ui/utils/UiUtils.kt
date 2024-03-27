package com.example.composefirsttry.giftcard.ui.utils

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.composefirsttry.R
import com.example.composefirsttry.giftcard.ui.common.dialog.CustomDialog

object UiUtils {
    fun getRemoveCardDialog(activity: FragmentActivity, removeListener: View.OnClickListener): CustomDialog {
        return CustomDialog(activity)
            .setTitle(R.string.cards_remove_card_dialog_title)
            .setPositiveButton(R.string.cards_dialog_remove_button_text, removeListener)
            .setNegativeButton(R.string.cards_dialog_cancel_button_text) { }
    }
}