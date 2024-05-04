package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.repository.MDMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    fun saveSettingPassword(isTurnOn: Boolean) {
        viewModelScope.launch {
            mdmRepository.saveSettingPassword(isTurnOn)
        }
    }

    fun saveSettingBiometric(isTurnOn: Boolean) {
        viewModelScope.launch {
            mdmRepository.saveSettingBiometric(isTurnOn)
        }
    }
}