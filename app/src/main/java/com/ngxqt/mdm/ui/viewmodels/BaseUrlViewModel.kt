package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ngxqt.mdm.repository.MDMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class BaseUrlViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    suspend fun saveBaseUrl(baseUrl: String): Deferred<Unit> {
        return coroutineScope {
            async {
                mdmRepository.saveBaseUrl(baseUrl)
            }
        }
    }
}