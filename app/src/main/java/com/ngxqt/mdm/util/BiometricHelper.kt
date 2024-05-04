package com.ngxqt.mdm.util

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

object BiometricHelper {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    interface BiometricCallback {
        fun onAuthenticationSuccess()
        fun onAuthenticationError(errorCode: Int, errorMessage: String)
        fun onAuthenticationFailed()
    }

    fun initBiometric(fragmentActivity: FragmentActivity, callback: BiometricCallback): BiometricPrompt {
        executor = ContextCompat.getMainExecutor(fragmentActivity)
        biometricPrompt = BiometricPrompt(fragmentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback.onAuthenticationError(errorCode, errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback.onAuthenticationSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Yêu cầu xác thực sinh trắc học")
            .setSubtitle("Bạn cần xác thực bằng sinh trắc học để tiếp tục")
            .setNegativeButtonText("Hủy")
            .build()

        return biometricPrompt
    }

    fun authenticate(biometricPrompt: BiometricPrompt) {
        biometricPrompt.authenticate(promptInfo)
    }

    fun checkDeviceHasBiometric(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(context,"Thiết bị không hỗ trợ tính năng sinh trắc học", Toast.LENGTH_SHORT).show()
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(context,"Tính năng sinh trắc học không khả dụng", Toast.LENGTH_SHORT).show()
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(context,"Thiết bị chưa có thông tin sinh trắc học", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                Toast.makeText(context,"Thiết bị chưa có thông tin sinh trắc học", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}