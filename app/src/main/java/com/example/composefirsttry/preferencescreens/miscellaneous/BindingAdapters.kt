package com.example.composefirsttry.preferencescreens.miscellaneous

import androidx.databinding.BindingAdapter
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox

@BindingAdapter("bindState")
fun bindState(triCheckBox: TriCheckBox, state: Int) {
    triCheckBox.setState(state)
}