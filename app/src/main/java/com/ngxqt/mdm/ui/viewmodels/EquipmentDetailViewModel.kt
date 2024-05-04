package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.GetListInventoryResponse
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
class EquipmentDetailViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _getListInventoryResponseLiveData: MutableLiveData<Event<Resource<GetListInventoryResponse>>> = MutableLiveData()
    val getListInventoryResponseLiveData: LiveData<Event<Resource<GetListInventoryResponse>>>
        get() = _getListInventoryResponseLiveData

    private var getListInventorysResponse: GetListInventoryResponse? = null

    fun getListInventory(authorization: String, equipmentId: Int) = viewModelScope.launch() {
        safeGetListInventory(authorization, equipmentId)
    }

    private suspend fun safeGetListInventory(authorization: String, equipmentId: Int) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.getListInventoryById(authorization, equipmentId)
                _getListInventoryResponseLiveData.postValue(Event(handleGetAllUsersResponse(response)))
            } else {
                _getListInventoryResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("GETLISTINVENTORY_API_ERROR", e.toString())
            _getListInventoryResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetAllUsersResponse(response: Response<GetListInventoryResponse>): Resource<GetListInventoryResponse> {
        if (response.isSuccessful) {
            Log.d("GETLISTINVENTORY_API_SUCCESS", response.body()?.dataLength.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getListInventorysResponse ?: resultResponse)
            }
        } else {
            Log.e("GETLISTINVENTORY_API_ERROR", response.toString())
        }
        return Resource.Error((getListInventorysResponse ?: response.message()).toString())
    }
}