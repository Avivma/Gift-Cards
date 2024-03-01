package com.example.composefirsttry.utils

import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("bindChecked")
fun bindChecked(view: View, checked: Boolean) {
    if (view is CheckedTextView) {
        //fade out card
        view.alpha = if (checked) 1.0f else 0.5f
    } else {
        //show cross off
        view.visibility = if (checked) View.INVISIBLE else View.VISIBLE
    }
}


@BindingAdapter("loadImage")
fun loadImage(image: ImageView, @DrawableRes imageRes:  Int) {
    if (imageRes != 0) {
        image.setImageDrawable(ContextCompat.getDrawable(image.context, imageRes))
    }
}

@BindingAdapter("setDiscount")
fun setDiscount(textView: TextView, discount: Float) {
    textView.text = "${discount.toString().removeSuffix(".0")}%"
}