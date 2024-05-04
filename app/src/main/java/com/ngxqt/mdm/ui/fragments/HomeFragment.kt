package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentHomeBinding
import com.ngxqt.mdm.ui.adapters.HomeGridAdapter
import com.ngxqt.mdm.ui.model.HomeItemModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var backPressedCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserInfo()
        setUpGridView()
        setBackPressed()
    }

    private fun setUserInfo() {
        val userPreferences = UserPreferences(requireContext())
        userPreferences.accessUserInfoFlow.asLiveData().observe(viewLifecycleOwner, Observer {
            val user = it
            binding.apply {
                Glide.with(root)
                    .load(user?.profilePhotoUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.logo)
                    .into(userImage)
                userName.text = "${user?.displayName?.trim()}"
            }
        })
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.menu_home).isChecked = true
    }

    private fun setUpGridView(){
        val itemModelArrayList: ArrayList<HomeItemModel?> = ArrayList()
        itemModelArrayList.add(HomeItemModel("Thiết Bị", R.drawable.ic_equipment_fill))
        itemModelArrayList.add(HomeItemModel("Báo Hỏng", R.drawable.ic_noti_ring_fill))
        itemModelArrayList.add(HomeItemModel("Khoa Phòng", R.drawable.ic_department_fill))
        itemModelArrayList.add(HomeItemModel("Nhân Viên", R.drawable.ic_staff_fill))
        itemModelArrayList.add(HomeItemModel("Thống Kê", R.drawable.ic_statistical))
        itemModelArrayList.add(HomeItemModel("Kiểm Kê", R.drawable.ic_inventory_fill))
        itemModelArrayList.add(HomeItemModel("Quản Lý Kho",R.drawable.ic_quan_ly_kho))
        itemModelArrayList.add(HomeItemModel("Đấu Thầu",R.drawable.ic_bidding))

        binding.homeGridview.adapter = HomeGridAdapter(requireContext(),itemModelArrayList)

        binding.homeGridview.setOnItemClickListener { _, view, position, id ->
            view.alpha = 0.5f
            Handler(Looper.getMainLooper()).postDelayed({
                view.alpha = 1.0f
            }, 100)
            if (position == 0){
                findNavController().navigate(R.id.action_homeFragment_to_equipmentsFragment)
            } else if (position == 1){
                findNavController().navigate(R.id.action_homeFragment_to_brokenFragment)
            } else if (position == 2){
                findNavController().navigate(R.id.action_homeFragment_to_departmentFragment)
            } else if (position == 3){
                findNavController().navigate(R.id.action_homeFragment_to_staffFragment)
            } else if (position == 4){
                //findNavController().navigate(R.id.action_homeFragment_to_statisticalFragment)
                //Toast.makeText(requireContext(),"Thống kê", Toast.LENGTH_SHORT).show()
            } else if (position == 5){
                findNavController().navigate(R.id.action_homeFragment_to_inventoryFragment)
            }
        }
        binding.btnHomeNoti.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notiFragment)
        }
    }

    private fun setBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (backPressedCount == 1){
                requireActivity().finishAffinity()
            } else {
                backPressedCount++
                Toast.makeText(requireContext(), "Nhấn hai lần để thoát ứng dụng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}