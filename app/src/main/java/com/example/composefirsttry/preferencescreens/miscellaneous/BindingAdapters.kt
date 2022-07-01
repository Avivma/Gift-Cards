package com.example.composefirsttry.preferencescreens.miscellaneous

import android.view.View
import android.widget.CheckedTextView
import androidx.databinding.BindingAdapter
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox

@BindingAdapter("bindState")
fun bindState(triCheckBox: TriCheckBox, state: Int) {
    triCheckBox.setState(state)
}

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