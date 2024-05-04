package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.authenticate
import com.ngxqt.mdm.util.BiometricHelper.initBiometric
import com.ngxqt.mdm.util.observeOnce

class SplashFragment : Fragment(), BiometricHelper.BiometricCallback {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        val biometricPrompt = initBiometric(requireActivity(),this)
        Handler(Looper.getMainLooper()).postDelayed({
            UserPreferences(requireContext()).accessSettingPassword.asLiveData().observeOnce(viewLifecycleOwner, Observer { isSavePassswordTurnedOn ->
                if (isSavePassswordTurnedOn == true){
                    UserPreferences(requireContext()).accessSettingBiometric.asLiveData().observeOnce(viewLifecycleOwner, Observer { isBiometricTurnedOn ->
                        if (isBiometricTurnedOn == true) authenticate(biometricPrompt)
                        else findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    })
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_baseUrlFragment5)
                }
            })

        }, 500)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onAuthenticationSuccess() {
        Toast.makeText(requireContext(), "Xác thực thành công", Toast.LENGTH_SHORT).show()
        Log.d("SplashFragment","onAuthenticationSuccess")
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        requireActivity().finishAffinity()
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}