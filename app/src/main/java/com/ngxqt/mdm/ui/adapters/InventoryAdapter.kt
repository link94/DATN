package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.Inventory
import com.ngxqt.mdm.databinding.ItemInventoryBinding

class InventoryAdapter(): ListAdapter<Inventory,InventoryAdapter.InventoryVewHolder>(COMPARATOR) {

    inner class InventoryVewHolder(private val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(inventory: Inventory) {
            binding.apply {
                inventoryNote.text = inventory.note?.trim()
                inventoryDate.text = inventory.updatedAt?.trim()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryVewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryVewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryVewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    fun getItemHeight(parent: ViewGroup): Int {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        itemView.measure(widthMeasureSpec, heightMeasureSpec)
        return itemView.measuredHeight
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Inventory>() {
            override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
                return oldItem == newItem
            }

        }
    }
}