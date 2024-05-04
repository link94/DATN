package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.GetNotificationResponse
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil.Companion.hasInternetConnection
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _getNotificationResponseLiveData: MutableLiveData<Event<Resource<GetNotificationResponse>>> = MutableLiveData()
    val getNotificationResponseLiveData: LiveData<Event<Resource<GetNotificationResponse>>>
        get() = _getNotificationResponseLiveData

    private var getNotificationResponse: GetNotificationResponse? = null

    fun getNotification(authorization: String) = viewModelScope.launch() {
        safeGetNotification(authorization)
    }

    private suspend fun safeGetNotification(authorization: String) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.getNotification(authorization)
                _getNotificationResponseLiveData.postValue(Event(handleGetNotificationResponse(response)))
            } else {
                _getNotificationResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("GETNOTIFICATION_API_ERROR", e.message.toString())
            _getNotificationResponseLiveData.postValue(Event(Resource.Error(e.message.toString())))
        }
    }

    private fun handleGetNotificationResponse(response: Response<GetNotificationResponse>): Resource<GetNotificationResponse> {
        if (response.isSuccessful) {
            Log.d("GETNOTIFICATION_RETROFIT_SUCCESS", response.body()?.total.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getNotificationResponse ?: resultResponse)
            }
        } else {
            Log.e("GETNOTIFICATION_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getNotificationResponse ?: response.message()).toString())
    }

}