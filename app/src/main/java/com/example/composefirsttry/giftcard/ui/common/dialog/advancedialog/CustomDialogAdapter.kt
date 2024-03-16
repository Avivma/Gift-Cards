package com.example.composefirsttry.giftcard.ui.common.dialog.advancedialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.databinding.CardCustomDialogRowBinding

class CustomDialogAdapter (private val items: List<CustomDialogAdapterItem>, @LayoutRes private val layoutId: Int) : RecyclerView.Adapter<CustomDialogAdapter.ItemHolder>() {
    var itemClickedLivedata: LiveData<CustomDialogAdapterItem> = MutableLiveData()
    private var itemClickedMutableLivedata: MutableLiveData<CustomDialogAdapterItem> = itemClickedLivedata as MutableLiveData<CustomDialogAdapterItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding: CardCustomDialogRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        return ItemHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = items[position]
        holder.binding(item)
    }
    //View Holder
    inner class ItemHolder(private val binding: CardCustomDialogRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun binding(item: CustomDialogAdapterItem) {
            //set binding variables
            binding.item = item
            //set listeners
            binding.rowLayout.setOnClickListener {
                itemClickedMutableLivedata.postValue(item)
            }
        }
    }
}