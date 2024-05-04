package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.RequestEquipmentInventoryPost
import com.ngxqt.mdm.data.model.RequestEquipmentInventoryResponse
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
class InventoryNoteViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _inventoryNoteResponseLiveData: MutableLiveData<Event<Resource<RequestEquipmentInventoryResponse>>> = MutableLiveData()
    val inventoryNoteResponseLiveData: LiveData<Event<Resource<RequestEquipmentInventoryResponse>>>
        get() = _inventoryNoteResponseLiveData

    private var inventoryNoteResponse: RequestEquipmentInventoryResponse? = null

    fun inventoryNote(authorization: String, equipmentId: Int, post: RequestEquipmentInventoryPost) = viewModelScope.launch() {
        safeInventoryNote(authorization, equipmentId, post)
    }

    private suspend fun safeInventoryNote(authorization: String, equipmentId: Int, post: RequestEquipmentInventoryPost) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.requestEquipmentInventory(authorization, equipmentId, post)
                _inventoryNoteResponseLiveData.postValue(Event(handleInventoryNoteResponse(response)))
            } else {
                _inventoryNoteResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception){
            Log.e("INVENTORY_API_ERROR", e.toString())
            _inventoryNoteResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }

    }

    private fun handleInventoryNoteResponse(response: Response<RequestEquipmentInventoryResponse>): Resource<RequestEquipmentInventoryResponse> {
        if (response.isSuccessful) {
            Log.d("INVENTORY_RETROFIT_SUCCESS", "OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(inventoryNoteResponse ?: resultResponse)
            }
        } else {
            Log.e("INVENTORY_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((inventoryNoteResponse ?: response.message()).toString())
    }
}