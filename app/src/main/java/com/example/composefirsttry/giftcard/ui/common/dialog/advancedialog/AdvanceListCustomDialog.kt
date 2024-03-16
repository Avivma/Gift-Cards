package com.example.composefirsttry.giftcard.ui.common.dialog.advancedialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.R
import com.example.composefirsttry.giftcard.ui.common.dialog.CustomDialog

class AdvanceListCustomDialog(mActivity: FragmentActivity): CustomDialog(mActivity){
    private lateinit var adapter: CustomDialogAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setRecyclerView()
        observeAdapterLiveData()
        binding.model = model
        return dialog
    }

    private fun setRecyclerView() {
        adapter = CustomDialogAdapter(model.items, R.layout.card_selectable_custom_dialog_row)
        adapter.setHasStableIds(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(mActivity)
    }

    private fun observeAdapterLiveData() {
        adapter.itemClickedLivedata.observe(this, Observer { item ->
            val selectableItem = item as CustomDialogAdapterItem.Selectable
            selectableItem.selected = !selectableItem.selected
            model.itemsListener!!.invoke(item)
            this.dismiss()
        })
    }

    fun setAdapter(items: List<CustomDialogAdapterItem.Selectable>, listener: ((CustomDialogAdapterItem) -> Unit)): AdvanceListCustomDialog {
        model.items = items
        model.itemsListener = listener
        model.itemsVisible = true
        return this
    }
}