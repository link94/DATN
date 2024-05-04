package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.RequestEquipmentBrokenPost
import com.ngxqt.mdm.databinding.FragmentBrokenReportBinding
import com.ngxqt.mdm.ui.viewmodels.BrokenReportViewModel
import com.ngxqt.mdm.util.BASE_URL_KA
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.authenticate
import com.ngxqt.mdm.util.BiometricHelper.initBiometric
import com.ngxqt.mdm.util.Resource
import com.ngxqt.mdm.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BrokenReportFragment : Fragment(), BiometricHelper.BiometricCallback {
    private val viewModel: BrokenReportViewModel by viewModels()
    private var _binding: FragmentBrokenReportBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<BrokenReportFragmentArgs>()
    private var reportBroken = false
    private lateinit var biometricPrompt: BiometricPrompt
    private var isTurnedOn: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrokenReportBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        biometricPrompt = initBiometric(requireActivity(),this)
        setEquipmentDetail()

    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Báo Hỏng")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setEquipmentDetail(){
        UserPreferences(requireContext()).accessSettingBiometric.asLiveData().observeOnce(viewLifecycleOwner, Observer {
            isTurnedOn = it
        })
        val equipment = args.equipment
        binding.apply {
            val imgPath = equipment.path ?: equipment.urlImg?.substringAfterLast("/") ?: ""
            Glide.with(root)
                .load(BASE_URL_KA +"/public/uploads/"+imgPath)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.logo)
                .into(equipDetailImage)
            equipDetailTitle.text = equipment.title
            equipDetailStatus.text = equipment.status.let {
                if (it == "active") {"Đang Sử Dụng"}
                else if (it == "was_broken") {"Đang Báo Hỏng"}
                else if (it == "corrected") {"Đang Sửa Chữa"}
                else if (it == "liquidated") {"Đã Thanh Lý"}
                else if (it == "inactive") {"Ngừng Sử Dụng"}
                else if (it == "not_handed") {"Mới"}
                else {""}
            }
            equipDetailModel.text = equipment.model
            equipDetailSerial.text = equipment.serial
            equipDetailYearManufacture.text = equipment.yearManufacture
            equipDetailYearUse.text = equipment.yearUse
            equipDetailManufacturer.text = equipment.manufacturer
            equipDetailOrigin.text = equipment.origin
            if (equipment.status == "active"){
                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
            }
            btnBrokenReport.setOnClickListener {
                val reason = binding.editTextReason.text.toString().trim()
                if (reason.isNotEmpty()){
                    if (isTurnedOn == true) authenticate(biometricPrompt)
                    else equipment.id?.let { requestBroken(it, reason) }
                } else{
                    Toast.makeText(requireContext(), "Vui Lòng Nhập Lí Do Báo Hỏng", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun requestBroken(id: Int, reason: String) {
        //call api
        val calendar = Calendar.getInstance()
        val dateFailure = SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(calendar.time)
        val requestEquipmentBrokenPost = RequestEquipmentBrokenPost(dateFailure, reason)
        val userPreferences = UserPreferences(requireContext())
        if (reportBroken == false) {
            lifecycleScope.launch {
                userPreferences.accessTokenString()?.let { viewModel.brokenReport(it,id, requestEquipmentBrokenPost) }
                binding.paginationProgressBar.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(requireContext(),"Đã Báo Hỏng Thiết Bị", Toast.LENGTH_SHORT).show()
        }
        //lắng nghe livedate response
        viewModel.brokenReportResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when (it) {
                    is Resource.Success -> {
                        if (it.data?.status == "200"){
                            Toast.makeText(requireContext(),"Báo Hỏng Thành Công", Toast.LENGTH_SHORT).show()
                            binding.apply {
                                equipDetailStatus.text = "Đang Báo Hỏng"
                                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
                            }
                            reportBroken =  true
                            binding.tvBrokenReportError.visibility = View.GONE
                        } else {
                            Toast.makeText(requireContext(),"Báo Hỏng Thất Bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvBrokenReportError.visibility = View.VISIBLE
                        binding.tvBrokenReportError.setText("ERROR\n${it.message}")
                        Log.e("BROKENREPORT_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    override fun onAuthenticationSuccess() {
        Log.d("BrokenReportFragment","onAuthenticationSuccess")
        val reason = binding.editTextReason.text.toString().trim()
        args.equipment.id?.let { requestBroken(it, reason) }
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}