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
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.databinding.FragmentStaffBinding
import com.ngxqt.mdm.ui.adapters.StaffAdapter
import com.ngxqt.mdm.ui.viewmodels.StaffViewModel
import com.ngxqt.mdm.util.Resource
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StaffFragment : Fragment(),StaffAdapter.OnItemClickListener {
    private val viewModel: StaffViewModel by viewModels()
    private var _binding: FragmentStaffBinding? = null
    private val binding get() = _binding!!
    private val staffAdapter = StaffAdapter(this)
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStaffBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        searchUser(null)

        binding.btnStaffSearch.setOnClickListener {
            val keyword = binding.editTextStaffSearch.text.toString().trim()
            if (keyword.isNotEmpty()){
                searchUser(keyword)
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Nhân Viên")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewStaff.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = staffAdapter
        }
    }

    private fun searchUser(keyword: String?) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.searchUsers(it,keyword) }

        }
        //Get LiveData
        viewModel.searchUsersResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {

                when(it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.INVISIBLE
                        binding.tvStaffError.visibility = View.GONE
                        val data = it.data?.data
                        if (data?.isNotEmpty() == true){
                            staffAdapter.submitList(data)
                            binding.tvStaffError.visibility = View.GONE
                        }else{
                            Toast.makeText(requireContext(), "Không Tìm Thấy Nhân Viên Có Từ Khóa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.paginationProgressBar.visibility = View.INVISIBLE
                        binding.tvStaffError.visibility = View.VISIBLE
                        binding.tvStaffError.setText("ERROR\n${it.message}")
                        Log.e("SEARCHSTAFF_OBSERVER_ERROR", it.data.toString())
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onEmailClick(user: User) {
        val intent= Intent(Intent.ACTION_SENDTO)
        intent.setData(Uri.parse("mailto:${user.email}"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "[Hệ thống quản lý thiết bị y tế]")
        intent.putExtra(Intent.EXTRA_TEXT, "Kính gửi ${user.name}")
        val chooser = Intent.createChooser(intent, "Chọn ứng dụng để thực hiện gửi mail:")
        startActivity(chooser)
    }

    override fun onPhoneClick(user: User) {
        val rxPermissions = RxPermissions(this)

        disposable = rxPermissions
            .request(
                Manifest.permission.CALL_PHONE
            )
            .subscribe { granted ->
                if (granted) {
                    callPhone(user.phone)
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
}