package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.databinding.FragmentBrokenBinding
import com.ngxqt.mdm.ui.adapters.equipment.EquipmentLoadStateAdapter
import com.ngxqt.mdm.ui.adapters.equipment.EquipmentsPagingAdapter
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrokenFragment : Fragment(),
    EquipmentsPagingAdapter.OnItemClickListener {
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentBrokenBinding? = null
    private val binding get() = _binding!!
    private val equipmentsPagingAdapter = EquipmentsPagingAdapter(this)
    private var filterKeyword: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrokenBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        if (filterKeyword != null) {
            getEquipments("active",filterKeyword, null)
        }

        binding.btnBrokenScan.setOnClickListener {
            findNavController().navigate(R.id.action_brokenFragment_to_scanFragment)
        }

        binding.btnBrokenSearch.setOnClickListener {
            filterKeyword = binding.editTextEquipmentsSearch.text.toString().trim()
            if (filterKeyword!!.isNotEmpty()){
                getEquipments("active",filterKeyword, null)
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Báo Hỏng")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewBroken.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = equipmentsPagingAdapter.withLoadStateHeaderAndFooter(
                header = EquipmentLoadStateAdapter { equipmentsPagingAdapter.retry() },
                footer = EquipmentLoadStateAdapter { equipmentsPagingAdapter.retry() }
            )
        }
        binding.buttonRetry.setOnClickListener {
            equipmentsPagingAdapter.retry()
            getEquipments("active",filterKeyword, null)
        }
        equipmentsPagingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                imageFind.isVisible = false
                textViewFind.isVisible = false
                paginationProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewBroken.isVisible = loadState.source.refresh is LoadState.NotLoading
                cardViewRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                imageError.isVisible = loadState.source.refresh is LoadState.Error

                //Empty View
                if(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && equipmentsPagingAdapter.itemCount <= 0){
                    recyclerViewBroken.isVisible = false
                    imageEmpty.isVisible = true
                    textViewEmpty.isVisible = true
                    imageFind.isVisible = false
                    textViewFind.isVisible = false
                } else {
                    imageEmpty.isVisible = false
                    textViewEmpty.isVisible = false
                }
            }
        }
    }



    private fun getEquipments(status: String?, keyword: String?, departmentId: Int?){
        // Call API
        lifecycleScope.launch {
            UserPreferences(requireContext()).accessTokenString()?.let { viewModel.getEquipments(it,status,keyword,departmentId).observe(viewLifecycleOwner){
                equipmentsPagingAdapter.submitData(lifecycle,it)
            }}
        }
    }

    override fun onItemClick(equipment: Equipment) {
        val action = BrokenFragmentDirections.actionBrokenFragmentToBrokenReportFragment(equipment)
        findNavController().navigate(action)
    }


}