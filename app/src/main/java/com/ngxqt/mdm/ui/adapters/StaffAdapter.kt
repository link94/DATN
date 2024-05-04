package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.databinding.ItemStaffBinding

class StaffAdapter(private val listener: OnItemClickListener): ListAdapter<User,StaffAdapter.StaffVewHolder>(STAFF_COMPARATOR) {

    inner class StaffVewHolder(private val binding: ItemStaffBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                staffMail.setOnClickListener {
                    listener.onEmailClick(getItem(bindingAdapterPosition))
                }
                staffPhone.setOnClickListener {
                    listener.onPhoneClick(getItem(bindingAdapterPosition))
                }
            }
        }

        fun bind(staff: User) {
            binding.apply {
                Glide.with(itemView)
                    .load(staff.profilePhotoUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.logo)
                    .into(staffImage)
                staffName.text = staff.displayName?.trim()
                staffMail.text = staff.email?.trim()
                staffPhone.text = staff.phone?.trim()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffVewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StaffVewHolder(binding)
    }

    interface OnItemClickListener {
        fun onEmailClick(user: User)
        fun onPhoneClick(user: User)
    }

    override fun onBindViewHolder(holder: StaffVewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    companion object {
        private val STAFF_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

        }
    }
}