package com.ngxqt.mdm.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.databinding.ItemStaffBinding

class StaffPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<User, StaffPagingAdapter.StaffViewHolder>(EVENT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StaffViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class StaffViewHolder(private val binding: ItemStaffBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val event = getItem(position)
                    if (event!=null)
                        listener.onItemClick(event)
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
                staffName.text = staff.displayName
                staffMail.text = staff.email
                staffPhone.text = staff.phone
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(staff: User)
    }

    companion object{
        private val EVENT_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}