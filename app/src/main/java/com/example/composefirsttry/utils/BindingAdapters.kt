package com.example.composefirsttry.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.composefirsttry.R
import com.example.composefirsttry.giftcard.common.fetchimages.GlideHandler
import com.example.composefirsttry.giftcard.ui.main.StoreMainTextModel

@BindingAdapter("loadImage")
fun loadImage(image: ImageView, @DrawableRes imageRes:  Int) {
    if (imageRes != 0) {
        image.setImageDrawable(ContextCompat.getDrawable(image.context, imageRes))
    }
}

@BindingAdapter("setAlpha")
fun setAlpha(view: View, selected: Boolean) {
    view.alpha = if (selected) 1.0f else 0.5f
}

@BindingAdapter("loadImage")
fun loadImage(image: ImageView, imageUrl: String? = null) {
    if (imageUrl != null) {
        GlideHandler.loadImage(image, imageUrl)
    }
}

@BindingAdapter("setDiscount")
fun setDiscount(textView: TextView, discount: Float) {
    textView.text = "${discount.toString().removeSuffix(".0")}%"
}


@BindingAdapter("setCardSelectedText")
fun setCardSelectedText(view: TextView, textModel: StoreMainTextModel?) {
    if (textModel != null) {
        if (textModel.allCardsSelected) view.setText(R.string.all_cards_selected_text)
        else view.text = view.context.resources.getString(
            R.string.cards_not_selected_text_exe,
            textModel.selectedCards.toString(),
            textModel.cards.toString()
        )
    }
}