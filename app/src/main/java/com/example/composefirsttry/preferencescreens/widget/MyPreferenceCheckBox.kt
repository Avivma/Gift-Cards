package com.example.composefirsttry.preferencescreens.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceViewHolder
import com.example.composefirsttry.R

internal class MyPreferenceCheckBox : CheckBoxPreference {
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
    }
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context!!, attrs, defStyleAttr, defStyleRes) {
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?) : super(context!!) {}

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.findViewById(R.id.stateLayoutCL).setOnClickListener {
            (holder.findViewById(R.id.checkBox) as TriCheckBox).performClick()
        }
        listenersToBind?.invoke(holder)
    }

    private var listenersToBind: ((PreferenceViewHolder) -> Unit)? = null

    fun setOnBindViewHolder(listenersToBind: (PreferenceViewHolder) -> Unit) {
        this.listenersToBind = listenersToBind
    }
}