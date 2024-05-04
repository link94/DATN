package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetAllDepartmentsResponse(
    @SerializedName("status_code")
    var status: Int,
    @SerializedName("data")
    var data: MutableList<Department>,
    @SerializedName("dataLength")
    var dataLength: Int
)
