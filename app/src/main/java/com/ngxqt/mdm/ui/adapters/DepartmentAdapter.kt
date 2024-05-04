package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.mdm.data.model.Department
import com.ngxqt.mdm.databinding.ItemDepartmentBinding

class DepartmentAdapter(private val listener: OnItemClickListener): ListAdapter<Department,DepartmentAdapter.DepartmentVewHolder>(COMPARATOR) {

    inner class DepartmentVewHolder(private val binding: ItemDepartmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                departmentEmail.setOnClickListener {
                    listener.onEmailClick(getItem(bindingAdapterPosition))
                }
                departmentPhone.setOnClickListener {
                    listener.onPhoneClick(getItem(bindingAdapterPosition))
                }
                departmentListEquipment.setOnClickListener {
                    listener.onListEquipClick(getItem(bindingAdapterPosition))
                }
            }
        }

        fun bind(department: Department) {
            binding.apply {
                departmentTitle.text = department.title?.trim()
                departmentEmailText.text = department.email?.trim()
                departmentPhoneText.text = department.phone?.trim()
                departmentAddressText.text = department.address?.trim()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentVewHolder {
        val binding = ItemDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentVewHolder(binding)
    }

    interface OnItemClickListener {
        fun onEmailClick(department: Department)
        fun onPhoneClick(department: Department)
        fun onListEquipClick(department: Department)
    }

    override fun onBindViewHolder(holder: DepartmentVewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Department>() {
            override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean {
                return oldItem == newItem
            }

        }
    }
}