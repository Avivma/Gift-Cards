package com.example.composefirsttry.giftcard.ui.common.dialog.advancedialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.composefirsttry.giftcard.ui.common.dialog.CustomDialog
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem

class AdvanceListCustomDialog(mActivity: FragmentActivity): CustomDialog(mActivity){
    private lateinit var adapter: AdvanceCustomDialogAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setRecyclerView()
        observeAdapterLiveData()
        binding.model = model
        return dialog
    }

    private fun setRecyclerView() {
        adapter = AdvanceCustomDialogAdapter(model.items)
        adapter.setHasStableIds(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(mActivity)
    }

    private fun observeAdapterLiveData() {
        adapter.itemClickedLivedata.observe(this, Observer { item ->
            model.itemsListener!!.invoke(item)
        })
    }

    fun setAdapter(items: List<CustomDialogAdapterItem.Selectable>, listener: ((CustomDialogAdapterItem) -> Unit)): AdvanceListCustomDialog {
        model.items = items
        model.itemsListener = listener
        model.itemsVisible = true
        return this
    }
}