package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentSettingBinding
import com.ngxqt.mdm.ui.viewmodels.SettingViewModel
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.authenticate
import com.ngxqt.mdm.util.BiometricHelper.checkDeviceHasBiometric
import com.ngxqt.mdm.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment(), BiometricHelper.BiometricCallback {
    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var biometricPrompt: BiometricPrompt
    private var isBiometricTurnedOn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        biometricPrompt = BiometricHelper.initBiometric(requireActivity(), this)
        setToggleButton()
        toggleButtonCheckedListener()
    }

    private fun toggleButtonCheckedListener() {
        binding.settingSavePassword.setOnClickListener {
            val isChecked = binding.settingSavePassword.isChecked
            viewModel.saveSettingPassword(isChecked)
            if (isChecked) Toast.makeText(requireContext(), "Đã bật lưu mật khẩu", Toast.LENGTH_SHORT).show()
            else Toast.makeText(requireContext(), "Đã tắt lưu mật khẩu", Toast.LENGTH_SHORT).show()
        }
        binding.settingBiometric.setOnClickListener {
            if(checkDeviceHasBiometric(requireContext())){
                isBiometricTurnedOn  = binding.settingBiometric.isChecked
                lifecycleScope.launch {
                    UserPreferences(requireContext()).accessSettingBiometricBoolean().let {
                        if (it == true){
                            viewModel.saveSettingBiometric(isBiometricTurnedOn)
                            if (!isBiometricTurnedOn) Toast.makeText(requireContext(), "Đã tắt xác thực vân tay", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("SettingFragment","isBiometricIsTurnedOn $isBiometricTurnedOn")
                            authenticate(biometricPrompt)
                        }
                    }
                }
            } else {
                binding.settingBiometric.isChecked = false
            }
        }
    }

    private fun setToggleButton(){
        UserPreferences(requireContext()).accessSettingPassword.asLiveData().observeOnce(viewLifecycleOwner, Observer { isTurnedOn ->
            binding.settingSavePassword.isChecked = isTurnedOn == true
        })
        UserPreferences(requireContext()).accessSettingBiometric.asLiveData().observeOnce(viewLifecycleOwner, Observer { isTurnedOn ->
            binding.settingBiometric.isChecked = isTurnedOn == true
        })
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Cài Đặt")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onAuthenticationSuccess() {
        Log.d("SettingFragment","onAuthenticationSuccess $isBiometricTurnedOn")
        viewModel.saveSettingBiometric(isBiometricTurnedOn)
        if (isBiometricTurnedOn) Toast.makeText(requireContext(), "Xác thực thành công\nĐã bật xác thực vân tay", Toast.LENGTH_SHORT).show()
        else Toast.makeText(requireContext(), "Xác thực thành công\nĐã tắt xác thực vân tay", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        Log.d("SettingFragment","onAuthenticationError $errorMessage ${!isBiometricTurnedOn}")
        binding.settingBiometric.isChecked = !isBiometricTurnedOn
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Log.d("SettingFragment","onAuthenticationFailed")
        binding.settingBiometric.isChecked = !isBiometricTurnedOn
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}