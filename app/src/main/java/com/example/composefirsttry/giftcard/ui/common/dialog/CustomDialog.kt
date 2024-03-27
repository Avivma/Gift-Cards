package com.example.composefirsttry.giftcard.ui.common.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.DialogCustomBinding

open class CustomDialog(protected val mActivity: FragmentActivity) : DialogFragment() {
    protected lateinit var binding: DialogCustomBinding
    protected val model: CustomDialogModel = CustomDialogModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(mActivity.layoutInflater, R.layout.dialog_custom, null, false)!!
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(binding.root)
        val dialog = builder.create()
        setListeners()
        binding.model = model
        return dialog
    }

    private fun setListeners() {
        binding.positiveButton.setOnClickListener {
            val x = model.positiveButtonClickListener
            model.positiveButtonClickListener?.onClick(it)
            this.dismiss()
        }
        binding.negativeButton.setOnClickListener {
            model.negativeButtonClickListener?.onClick(it)
            this.dismiss()
        }
    }

    fun setTitle(@StringRes titleId: Int): CustomDialog {
        val title = mActivity.resources.getString(titleId)
        this.setTitle(title)
        return this
    }

    fun setTitle(title: String): CustomDialog {
        model.title = title
        return this
    }

    fun setMessage(@StringRes messageId: Int): CustomDialog {
        val message = mActivity.resources.getString(messageId)
        this.setMessage(message)
        return this
    }

    fun setMessage(message: String): CustomDialog {
        model.message = message
        model.messageVisible = true
        return this
    }

    fun setPositiveButton(@StringRes textId: Int,listener: View.OnClickListener): CustomDialog {
        model.positiveButtonText = mActivity.resources.getString(textId)
        model.positiveButtonVisible = true
        model.positiveButtonClickListener = listener
        return this
    }

    fun setNegativeButton(@StringRes textId: Int,listener: View.OnClickListener): CustomDialog {
        model.negativeButtonText = mActivity.resources.getString(textId)
        model.negativeButtonVisible = true
        model.negativeButtonClickListener = listener
        return this
    }

    fun setIsCancelable(cancelable: Boolean): CustomDialog {
        this.isCancelable = cancelable
        return this
    }

    fun show(tag: String? = "CustomDialog") {
        super.show(mActivity.supportFragmentManager, tag)
    }
}