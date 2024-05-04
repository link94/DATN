package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.databinding.ItemEquipmentBinding

class DialogRecyclerViewAdapter(private val listener: OnItemClickListener):
    ListAdapter<Equipment,DialogRecyclerViewAdapter.ViewHolder>(COMPARATOR) {

    inner class ViewHolder(private val binding: ItemEquipmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }
        fun bind(equipment: Equipment) {
            binding.apply {
                equipmentImage.setImageResource(R.drawable.logo)
                equipmentTitle.text = equipment.title
                equipmentModel.text = "Model: ${equipment.model?.trim()}"
                equipmentSerial.text = "Serial: ${equipment.serial?.trim()}"
                equipmentStatus.text = "Status: ${equipment.status?.trim()}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEquipmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    interface OnItemClickListener {
        fun onItemClick(equipment: Equipment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Equipment>() {
            override fun areItemsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
                return oldItem == newItem
            }

        }
    }
}