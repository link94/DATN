package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.LoginPost
import com.ngxqt.mdm.data.model.LoginResponse
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil.Companion.hasInternetConnection
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _loginResponseLiveData: MutableLiveData<Event<Resource<LoginResponse>>> = MutableLiveData()
    val loginResponseLiveData: LiveData<Event<Resource<LoginResponse>>>
        get() = _loginResponseLiveData

    private var loginResponse: LoginResponse? = null

    fun login(post: LoginPost) = viewModelScope.launch() {
        safeLogin(post)
    }

    private suspend fun safeLogin(post: LoginPost) {
        _loginResponseLiveData.postValue(Event(Resource.Loading()))
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.login(post)
                _loginResponseLiveData.postValue(Event(handleLoginResponse(response)))
            }else{
                _loginResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("LOGIN_API_ERROR", e.toString())
            when (e) {
                is IOException -> _loginResponseLiveData.postValue(Event(Resource.Error("Lỗi Mạng")))
                else ->_loginResponseLiveData.postValue(Event(Resource.Error("Lỗi")))
            }
        }
    }

    private fun handleLoginResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if (response.isSuccessful) {
            Log.d("LOGIN_RETROFIT_SUCCESS", "OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(loginResponse ?: resultResponse)
            }
        } else {
            Log.e("LOGIN_RETROFIT_ERROR", response.toString())
            if (response.code()==400){
                return Resource.Error("Tài Khoản Hoặc Mật Khẩu Không Đúng")
            }
        }
        return Resource.Error((loginResponse ?: response.message()).toString())
    }

    fun saveToken(accessToken: String) {
        viewModelScope.launch {
            mdmRepository.saveToken(accessToken)
        }
    }

    fun saveUserInfo(user: User) {
        viewModelScope.launch {
            mdmRepository.saveUserInfo(user)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            mdmRepository.clearData()
        }
    }
}