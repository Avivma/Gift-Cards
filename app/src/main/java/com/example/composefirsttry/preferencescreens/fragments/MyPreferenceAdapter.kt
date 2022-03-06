package com.example.composefirsttry.preferencescreens.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.PreferenceItemBinding
import com.example.composefirsttry.preferencescreens.PrefEnum
import com.example.composefirsttry.preferencescreens.PrefEvents

class MyPreferenceAdapter(private val listItem: ArrayList<MainPrefItem>): RecyclerView.Adapter<MyPreferenceAdapter.ItemViewHolder>() {
    private val moviePickedLiveData: MutableLiveData<PrefEvents>
        get() {
            TODO()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: PreferenceItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.preference_item, parent, false)
        this.setHasStableIds(true)
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

    fun setData(listItem: ArrayList<MainPrefItem>) {
        this.listItem.clear()
        listItem.addAll(listItem)
        this.notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: PreferenceItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(mainPrefItem: MainPrefItem) {
            binding.model = createModel(mainPrefItem)
            binding.stateLayoutCL.setOnClickListener {

            }
            binding.customizeButton.setOnClickListener {

            }
        }

        private fun createModel(mainPrefItem: MainPrefItem): MainPrefModel {
            var title: String = ""
            var description: String = ""
            when(mainPrefItem.prefType) {
                PrefEnum.SWEETS -> {
                    title = "sweets"
                    description = "Sugar rush"
                }
                PrefEnum.SALTS -> {
                    title = "salts"
                    description = "salty and delicious"
                }
            }
            return MainPrefModel(title, description, mainPrefItem.state)
        }
    }

    data class MainPrefModel(val title: String, val description: String, val state: Boolean)
}
