package com.example.composefirsttry.preferencescreens.fragments

import com.example.composefirsttry.preferencescreens.miscellaneous.ParentCategoryEnum
import java.io.Serializable

data class ParentPrefItem(
    val prefType: ParentCategoryEnum,
    var state: Int
) : Serializable