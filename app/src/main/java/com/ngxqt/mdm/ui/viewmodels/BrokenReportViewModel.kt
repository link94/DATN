package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.RequestEquipmentBrokenPost
import com.ngxqt.mdm.data.model.RequestEquipmentBrokenResponse
import com.ngxqt.mdm.util.CoroutineDispatcherProvider
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class BrokenReportViewModel @Inject constructor(
    private val mdmRepository: MDMRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _brokenReportResponseLiveData: MutableLiveData<Event<Resource<RequestEquipmentBrokenResponse>>> = MutableLiveData()
    val brokenReportResponseLiveData: LiveData<Event<Resource<RequestEquipmentBrokenResponse>>>
        get() = _brokenReportResponseLiveData

    private var brokenReportResponse: RequestEquipmentBrokenResponse? = null

    fun brokenReport(authorization: String, equipmentId: Int, post: RequestEquipmentBrokenPost) = viewModelScope.launch() {
        safeBrokenReport(authorization, equipmentId, post)
    }

    private suspend fun safeBrokenReport(authorization: String, equipmentId: Int, post: RequestEquipmentBrokenPost) {
        try {
            if(NetworkUtil.hasInternetConnection(context)){
                val response = mdmRepository.requestEquipmentBroken(authorization, equipmentId, post)
                _brokenReportResponseLiveData.postValue(Event(handleLoginResponse(response)))
            } else {
                _brokenReportResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception){
            Log.e("LOGIN_API_ERROR", e.toString())
            _brokenReportResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }

    }

    private fun handleLoginResponse(response: Response<RequestEquipmentBrokenResponse>): Resource<RequestEquipmentBrokenResponse> {
        if (response.isSuccessful) {
            Log.d("LOGIN_RETROFIT_SUCCESS", "OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(brokenReportResponse ?: resultResponse)
            }
        } else {
            Log.e("LOGIN_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((brokenReportResponse ?: response.message()).toString())
    }
}