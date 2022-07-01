package com.example.composefirsttry.preferencescreens.miscellaneous

import android.view.View
import androidx.databinding.BindingAdapter
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox

@BindingAdapter("bindState")
fun bindState(triCheckBox: TriCheckBox, state: Int) {
    triCheckBox.setState(state)
}

@BindingAdapter("bindChecked")
fun bindChecked(view: View, checked: Boolean) {
    view.visibility = if (checked) View.VISIBLE else View.INVISIBLE
}