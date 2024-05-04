package com.ngxqt.mdm.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.Department
import com.ngxqt.mdm.databinding.FragmentDepartmentBinding
import com.ngxqt.mdm.ui.adapters.DepartmentAdapter
import com.ngxqt.mdm.ui.viewmodels.DepartmentViewModel
import com.ngxqt.mdm.util.Resource
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DepartmentFragment : Fragment(), DepartmentAdapter.OnItemClickListener {
    private val viewModel: DepartmentViewModel by viewModels()
    private var _binding: FragmentDepartmentBinding? = null
    private val binding get() = _binding!!
    private val departmentAdapter = DepartmentAdapter(this)
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDepartmentBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getAllDepartments()
    }
    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Khoa Phòng")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewDepartment.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = departmentAdapter
        }
    }

    private fun getAllDepartments(){
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let {
                viewModel.getAllDepartments(it)
            }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllDepartmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        departmentAdapter.submitList(it.data?.data)
                        binding.tvDepartmentError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.tvDepartmentError.visibility = View.VISIBLE
                        binding.tvDepartmentError.setText("ERROR\n${it.message}")
                        Log.e("GETALLDEPARTMENT_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    override fun onEmailClick(department: Department) {
        val intent= Intent(Intent.ACTION_SENDTO)
        intent.setData(Uri.parse("mailto:${department.email}"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "[Hệ thống quản lý thiết bị y tế]")
        intent.putExtra(Intent.EXTRA_TEXT, "Kính gửi ${department.title}")
        val chooser = Intent.createChooser(intent, "Chọn ứng dụng để thực hiện gửi mail:")
        startActivity(chooser)
    }

    override fun onPhoneClick(department: Department) {
        val rxPermissions = RxPermissions(this)

        disposable = rxPermissions
            .request(
                Manifest.permission.CALL_PHONE
            )
            .subscribe { granted ->
                if (granted) {
                    callPhone(department.phone)
                } else {
                    Toast.makeText(requireContext(),"Hãy Chấp Thuận Quyền Quản Lý Cuộc Gọi",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun callPhone(phoneNumber: String?) {
        val intent= Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:$phoneNumber"))
        val chooser = Intent.createChooser(intent, "Chọn ứng dụng để thực hiện cuộc gọi:")
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(),"Permission denied",Toast.LENGTH_LONG).show()
            return
        }
        startActivity(chooser)
    }

    override fun onListEquipClick(department: Department) {
        val action = DepartmentFragmentDirections.actionDepartmentFragmentToDepartmentEquipmentFragment(department)
        findNavController().navigate(action)
    }
}