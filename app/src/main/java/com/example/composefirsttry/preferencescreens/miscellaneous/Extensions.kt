package com.example.composefirsttry.preferencescreens.miscellaneous

import androidx.fragment.app.Fragment


fun <T> Fragment.requireActivity() = requireActivity() as T
fun <K> Fragment.getApplication() = requireActivity().application as K