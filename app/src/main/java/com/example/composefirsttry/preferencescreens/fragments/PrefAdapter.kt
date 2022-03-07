package com.example.composefirsttry.preferencescreens.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.PreferenceItemBinding
import com.example.composefirsttry.preferencescreens.miscellaneous.ParentCategoryEnum
import com.example.composefirsttry.preferencescreens.miscellaneous.PrefEvents
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox

class PrefAdapter(private val listItem: ArrayList<ParentPrefItem>): RecyclerView.Adapter<PrefAdapter.ItemViewHolder>() {
    private val _intentionsLiveData: MutableLiveData<PrefEvents> = MutableLiveData()
    val intentionsLiveData: LiveData<PrefEvents> = _intentionsLiveData

    init {
        this.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: PreferenceItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.preference_item, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listItem[position])
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    //I used this method and setHasStableIds() in order to optimize RecyclerView performance. More info: https://stackoverflow.com/questions/44081579/sethasstableidstrue-in-recyclerview
    override fun getItemId(position: Int): Long {
        return listItem[position].prefType.hashCode().toLong()
    }

    fun setData(listItem: ArrayList<ParentPrefItem>) {
        this.listItem.clear()
        this.listItem.addAll(listItem)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: PreferenceItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(parentPrefItem: ParentPrefItem) {
            val model = createModel(parentPrefItem)
            binding.model = model

            binding.stateLayoutCL.setOnClickListener {
                binding.checkBox.performClick()
            }

            binding.checkBox.setOnStateChanged { state ->
                parentPrefItem.state = state
                _intentionsLiveData.postValue(PrefEvents.StateChanged(parentPrefItem))
            }

            binding.customizeButton.setOnClickListener {
                _intentionsLiveData.postValue(PrefEvents.Customize(parentPrefItem))
            }
        }

        private fun createModel(parentPrefItem: ParentPrefItem): MainPrefModel {
            var title: String = ""
            var description: String = ""
            when(parentPrefItem.prefType) {
                ParentCategoryEnum.SWEETS -> {
                    title = "Sweets"
                    description = "Sugar rush"
                }
                ParentCategoryEnum.SALTS -> {
                    title = "Salts"
                    description = "salty and delicious"
                }
            }
            return MainPrefModel(title, description, parentPrefItem.state)
        }
    }

    data class MainPrefModel(val title: String, val description: String, val state: Int)
}
