package com.example.composefirsttry.giftcard.ui.common.dialog.listdialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.giftcard.ui.common.dialog.CustomDialog
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem

class ListCustomDialog(mActivity: FragmentActivity): CustomDialog(mActivity){
    private lateinit var adapter: CustomDialogAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setRecyclerView()
        observeAdapterLiveData()
        determineVisibility()
        binding.model = model
        return dialog
    }

    private fun setRecyclerView() {
        adapter = CustomDialogAdapter(model.items)
        adapter.setHasStableIds(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(mActivity)
    }

    private fun observeAdapterLiveData() {
        adapter.itemClickedLivedata.observe(this, Observer { item ->
            model.itemsListener!!.invoke(item)
            this.dismiss()
        })
    }

    private fun determineVisibility() {
        model.itemsVisible = true
        model.positiveButtonVisible = false
        model.negativeButtonVisible = false
    }

    fun setAdapter(items: List<CustomDialogAdapterItem>, listener: ((CustomDialogAdapterItem) -> Unit)): ListCustomDialog {
        model.items = items
        model.itemsListener = listener
        return this
    }
}