package com.example.composefirsttry.preferencescreens.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.MutableLiveData
import com.example.composefirsttry.R

class TriCheckBox : androidx.appcompat.widget.AppCompatCheckBox {
    private var state = UNCHECKED
    var amountOfSubCB: Int = 0
    private var currentCheckedSubCB: Int = 0
    var subCbChecked: MutableLiveData<Boolean> = MutableLiveData()


    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        state = UNCHECKED
        updateBtn()
        setListenerWhenSubCbClicked()
        setOnclick()
    }

    private fun setListenerWhenSubCbClicked() {
        subCbChecked.observeForever { checked ->
            currentCheckedSubCB += if (checked) 1 else -1
            state = when (currentCheckedSubCB) {
                amountOfSubCB -> CHECKED
                0 -> UNCHECKED
                else -> INDETERMINATE
            }
            updateBtn()
        }
    }

    private fun setOnclick() {
        setOnCheckedChangeListener { buttonView, isChecked ->
            state = when (state) {
                CHECKED -> UNCHECKED
                UNCHECKED -> {
                    CHECKED
                }
                INDETERMINATE -> {
                    CHECKED
                }
                else -> CHECKED
            }
            onStateChange?.invoke(state)
            updateBtn()
        }
    }

    private fun updateBtn() {
        var btnDrawable: Int = R.drawable.ic_indeterminate
        btnDrawable = when (state) {
            INDETERMINATE -> R.drawable.ic_indeterminate
            UNCHECKED -> R.drawable.ic_unchecked
            CHECKED -> R.drawable.ic_checked
            else -> R.drawable.ic_unchecked
        }
        setButtonDrawable(btnDrawable)
    }

    fun getState(): Int {
        return state
    }

    fun setState(state: Int) {
        this.state = state
        updateBtn()
    }

    private var onStateChange: ((Int) -> Unit)? = null

    fun setOnStateChanged(onStateChange: (Int) -> Unit) {
        this.onStateChange = onStateChange
    }


    companion object {
        fun getName(state: Int): String = when(state) {
            UNCHECKED -> "Unchecked"
            INDETERMINATE -> "Indeterminate"
            CHECKED -> "Checked"
            else -> "Unchecked"
        }

        const val UNCHECKED = 0
        const val INDETERMINATE = 1
        const val CHECKED = 2
    }
}