package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.Department
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.databinding.FragmentEquipmentsBinding
import com.ngxqt.mdm.ui.adapters.equipment.EquipmentLoadStateAdapter
import com.ngxqt.mdm.ui.adapters.equipment.EquipmentsPagingAdapter
import com.ngxqt.mdm.ui.dialog.MyDialog
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EquipmentsFragment : Fragment(),
    EquipmentsPagingAdapter.OnItemClickListener{
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentEquipmentsBinding? = null
    private val binding get() = _binding!!
    private val equipmentsPagingAdapter = EquipmentsPagingAdapter(this)
    private var mutableListDepartment: MutableList<Department>? = null
    private var buttonDepartmentClickable = false
    private var isFirstRendered = false
    private var filterKeyword: String? = null
    private var filterStatus: String? = null
    private var filterDepartment: Int?  = null
    private var textButtonStatus: String? = null
    private var textButtonDepartment: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentsBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        //filterKeyword == null && filterStatus == null && filterDepartment == null
        if (!isFirstRendered){
            textButtonStatus = getString(R.string.tat_ca)
            textButtonDepartment = getString(R.string.tat_ca)
            getEquipments(filterStatus, filterKeyword, filterDepartment)
            getAllDepartment()
            isFirstRendered = true
        }

        setButtonClearFilter()

        binding.btnEquipmentsSearch.setOnClickListener {
            filterKeyword = binding.editTextEquipmentsSearch.text.toString().trim()
            if (filterKeyword!!.isNotEmpty()){
                getEquipments(filterStatus, filterKeyword, filterDepartment)
                setButtonClearFilter()
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEquipmentsFilterStatus.apply {
            setText(textButtonStatus)
            setOnClickListener {
                showDialogStatus()
            }
        }

        binding.btnEquipmentsFilterDepartment.apply {
            setText(textButtonDepartment)
            setOnClickListener {
                if (buttonDepartmentClickable){
                    showDialogDepartment()
                } else {
                    Toast.makeText(requireContext(),"Đợi Tải Dữ Liệu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnClearFilter.setOnClickListener {
            filterKeyword = null
            filterStatus = null
            filterDepartment = null
            binding.editTextEquipmentsSearch.text.clear()
            textButtonStatus = getString(R.string.tat_ca)
            binding.btnEquipmentsFilterStatus.setText(textButtonStatus)
            textButtonDepartment = getString(R.string.tat_ca)
            binding.btnEquipmentsFilterDepartment.setText(textButtonDepartment)
            getEquipments(filterStatus, filterKeyword, filterDepartment)
            binding.btnClearFilter.visibility = View.INVISIBLE
        }
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Thiết Bị")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewEquipments.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = equipmentsPagingAdapter.withLoadStateHeaderAndFooter(
                header = EquipmentLoadStateAdapter { equipmentsPagingAdapter.retry() },
                footer = EquipmentLoadStateAdapter { equipmentsPagingAdapter.retry() }
            )
        }
        binding.buttonRetry.setOnClickListener {
            equipmentsPagingAdapter.retry()
            getAllDepartment()
        }
        equipmentsPagingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                paginationProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewEquipments.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                imageError.isVisible = loadState.source.refresh is LoadState.Error

                //Empty View
                if(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && equipmentsPagingAdapter.itemCount <= 0){
                    recyclerViewEquipments.isVisible = false
                    imageEmpty.isVisible = true
                    textViewEmpty.isVisible = true
                } else {
                    imageEmpty.isVisible = false
                    textViewEmpty.isVisible = false
                }
            }
        }

    }

    private fun setButtonClearFilter(){
        if ( filterKeyword != null || filterStatus != null || filterDepartment != null){
            binding.btnClearFilter.visibility = View.VISIBLE
        } else {
            binding.btnClearFilter.visibility = View.INVISIBLE
        }
    }

    private fun showDialogStatus(){
        val statusList = mutableListOf("Tất Cả", "Đang Sử Dụng", "Đang Báo Hỏng", "Đang Sửa Chữa", "Đã Thanh Lý", "Ngưng Sử Dụng", "Mới")
        val dialog = MyDialog(statusList,"Lọc Trạng Thái", object : MyDialog.OnPickerItemSelectedListener{
            override fun onPickerItemSelected(position: Int) {
                textButtonStatus = statusList.get(position)
                binding.btnEquipmentsFilterStatus.setText(textButtonStatus)
                filterStatus = textButtonStatus.let {
                    if (it == "Đang Sử Dụng") {"active"}
                    else if (it == "Đang Báo Hỏng") {"was_broken"}
                    else if (it == "Đang Sửa Chữa") {"corrected"}
                    else if (it == "Đã Thanh Lý") {"liquidated"}
                    else if (it == "Ngưng Sử Dụng") {"inactive"}
                    else if (it == "Mới") {"not_handed"}
                    else if (it == "Tất Cả") {null}
                    else {null}
                }
                //filterKeyword = null
                getEquipments(filterStatus, filterKeyword, filterDepartment)
                setButtonClearFilter()
            }
        })
        dialog.show(parentFragmentManager,MyDialog.FILTER_DIALOG)
    }

    private fun showDialogDepartment(){
        val departmentList: MutableList<String> = mutableListOf("Tất cả")
        for (i in 0 until (mutableListDepartment?.size ?: 0)){
            departmentList.add(mutableListDepartment?.get(i)?.title.toString())
        }
        val dialog = MyDialog(departmentList,"Lọc Khoa Phòng", object : MyDialog.OnPickerItemSelectedListener{
            override fun onPickerItemSelected(position: Int) {
                textButtonDepartment = departmentList.get(position)
                binding.btnEquipmentsFilterDepartment.setText(textButtonDepartment)
                filterDepartment = position.let {
                    if (it==0){null}
                    else {mutableListDepartment?.get(position-1)?.id}
                }
                //filterKeyword = null
                getEquipments(filterStatus, filterKeyword, filterDepartment)
                setButtonClearFilter()
            }
        })
        dialog.show(parentFragmentManager,MyDialog.FILTER_DIALOG)
    }

    private fun getAllDepartment(){
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllDepartments(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllDepartmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { it ->
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        mutableListDepartment = it.data?.data
                        buttonDepartmentClickable = true
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        Log.e("GETALLDEPARTMENT_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
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
        val action = EquipmentsFragmentDirections.actionEquipmentsFragmentToEquipmentDetailFragment(equipment)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}