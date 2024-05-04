package com.ngxqt.mdm.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentUserBinding
import com.ngxqt.mdm.ui.viewmodels.UserViewModel
import com.ngxqt.mdm.util.BASE_URL
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.authenticate
import com.ngxqt.mdm.util.BiometricHelper.initBiometric
import com.ngxqt.mdm.util.Resource
import com.ngxqt.mdm.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment(), BiometricHelper.BiometricCallback {
    private val viewModel: UserViewModel by viewModels()
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val biometricPrompt = initBiometric(requireActivity(), this)
        setUserInfo()

        binding.emailLab.setOnClickListener {
            val emailLab = "ibme.lab@gmail.com"
            // Tạo Intent với hành động ACTION_SENDTO
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            // Đặt dữ liệu emailto: trong Intent
            emailIntent.data = Uri.parse("mailto:$emailLab")
            // Khởi chạy Intent để mở ứng dụng email
            startActivity(emailIntent)
        }

        binding.webAddress.setOnClickListener {
            // Địa chỉ website bạn muốn mở
//            val websiteUrl = "http://bvdemo.ibme.edu.vn"
            val websiteUrl = BASE_URL

            // Tạo Intent với hành động ACTION_VIEW
            val websiteIntent = Intent(Intent.ACTION_VIEW)
            // Đặt địa chỉ website vào dữ liệu của Intent
            websiteIntent.data = Uri.parse(websiteUrl)

            // Khởi chạy Intent để mở trình duyệt webf
            startActivity(websiteIntent)
        }

//        binding.userGuide.setOnClickListener {
//
//        }

        binding.userSetting.setOnClickListener {
            UserPreferences(requireContext()).accessSettingBiometric.asLiveData().observeOnce(viewLifecycleOwner, Observer { isTurnedOn ->
                if (isTurnedOn == true) authenticate(biometricPrompt)
                else findNavController().navigate(R.id.action_userFragment_to_settingFragment)
            })
        }
        binding.userLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.clearData()
                findNavController().navigate(R.id.action_userFragment_to_baseUrlFragment5)
            }
        }
    }

    private fun setUserInfo() {
        val userPreferences = UserPreferences(requireContext())
        var departmentId: MutableLiveData<Int?> = MutableLiveData(null)
        lifecycleScope.launch {
            val user = userPreferences.accessUserInfo()
            binding.apply {
                Glide.with(root)
                    .load(user?.profilePhotoUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.logo)
                    .into(userImage)
                userName.text = "${user?.displayName?.trim()?: "Không có dữ liệu"}"
                userPhone.text = "${user?.phone?.trim()?: "Không có dữ liệu"}"

                userBirthday.text = "${user?.birthday?.trim()?: "Không có dữ liệu"}"
                userGender.text = "${user?.gender?.trim()?: "Không có dữ liệu"}"
                userEmail.text = "${user?.email?.trim()?: "Không có dữ liệu"}"
                userAddress.text = "${user?.address?.trim()?: "Không có dữ liệu"}"
            }
            departmentId.value = user?.departmentId
        }
        departmentId.observeOnce(viewLifecycleOwner, Observer { id ->
            if (id == null) binding.userDepartment.text = "Không có dữ liệu"
            else setUserDepartment(id)
        })
    }

    private fun setUserDepartment(departmentId: Int?) {
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getDepartmentById(it,departmentId) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getDepartmentByIdResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.userDepartment.text = "${it.data?.data?.title?: "Không có dữ liệu"}"
                        //binding.tvDepartmentError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.e("GETALLDEPARTMENT_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.menu_user).isChecked = true
        binding.toolbar.toolbarTitle.setText("Cài đặt")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onAuthenticationSuccess() {
        Log.d("UserFragment","onAuthenticationSuccess")
        findNavController().navigate(R.id.action_userFragment_to_settingFragment)
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}