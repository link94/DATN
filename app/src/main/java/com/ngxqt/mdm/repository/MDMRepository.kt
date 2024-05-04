package com.ngxqt.mdm.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.*
import com.ngxqt.mdm.data.remote.ApiInterface
import com.ngxqt.mdm.ui.paging.EquipmentsPagingSource
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class MDMRepository @Inject constructor(
    private val mdmApi: ApiInterface,
    private val preferences: UserPreferences
) {
    suspend fun login(post: LoginPost): Response<LoginResponse> {
        return mdmApi.userLogin(post)
    }

    suspend fun getAllUsers(authorization: String): Response<GetAllUsersResponse> {
        return mdmApi.getAllUsers(authorization)
    }

    suspend fun searchUsers(authorization: String, keyword: String?): Response<GetAllUsersResponse> {
        return mdmApi.searchUsers(authorization, keyword)
    }

    fun getEquipments(authorization: String, status: String?, keyword: String?, departmentId: Int?) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { EquipmentsPagingSource(mdmApi, authorization, status, keyword, departmentId) }
        ).liveData

    suspend fun getAllEquipments(authorization: String): Response<GetAllEquipmentsResponse> {
        return mdmApi.getAllEquipments(authorization)
    }

    suspend fun searchEquipments(authorization: String, keyword: String): Response<GetAllEquipmentsResponse> {
        return mdmApi.searchEquipments(authorization, keyword)
    }

    suspend fun searchEquipmentsById(authorization: String, equipmentId: Int): Response<SearchEquipmentsByIdResponse> {
        return mdmApi.searchEquipmentsById(authorization, equipmentId)
    }

    suspend fun statisticalEquipments(authorization: String, status: String): Response<StatisticalEquipmentsResponse> {
        return mdmApi.statisticalEquipments(authorization, status)
    }

    suspend fun getAllDepartments(authorization: String): Response<GetAllDepartmentsResponse> {
        return mdmApi.getAllDepartments(authorization)
    }

    suspend fun getDepartmentById(authorization: String, departmentId: Int?): Response<GetDepartmentByIdResponse> {
        return mdmApi.getDepartmentById(authorization,departmentId)
    }

    suspend fun getListEquipmentsByDepartmenId(authorization: String, departmentId: Int): Response<GetListEquipmentsByDepartmentIdResponse> {
        return mdmApi.getListEquipmentsByDepartmenId(authorization, departmentId)
    }

    suspend fun getListInventoryById(authorization: String, equipmentId: Int): Response<GetListInventoryResponse> {
        return mdmApi.getListInventoryById(authorization, equipmentId)
    }

    suspend fun requestEquipmentBroken(authorization: String, equipmentId: Int, post: RequestEquipmentBrokenPost): Response<RequestEquipmentBrokenResponse> {
        return mdmApi.requestEquipmentBroken(authorization, equipmentId, post)
    }

    suspend fun requestEquipmentInventory(authorization: String, equipmentId: Int, post: RequestEquipmentInventoryPost): Response<RequestEquipmentInventoryResponse> {
        return mdmApi.requestEquipmentInventory(authorization, equipmentId, post)
    }

    suspend fun getNotification(authorization: String): Response<GetNotificationResponse> {
        return mdmApi.getNotification(authorization)
    }

    suspend fun saveToken(accessToken: String) {
        preferences.saveToken(accessToken)
    }

    suspend fun saveBaseUrl(baseUrl: String) {
        preferences.saveBaseUrl(baseUrl)
    }

    suspend fun saveUserInfo(user: User) {
        preferences.saveUserInfo(user)
    }

    suspend fun saveSettingPassword(isTurnOn: Boolean) {
        preferences.saveSettingPassword(isTurnOn)
    }

    suspend fun saveSettingBiometric(isTurnOn: Boolean) {
        preferences.saveSettingBiometric(isTurnOn)
    }

    suspend fun clearData() {
        preferences.clearData()
    }
}