package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetDepartmentByIdResponse(
    @SerializedName("status_code")
    var status: Int,
    @SerializedName("data")
    var data: Department,
    @SerializedName("dataLength")
    var dataLength: Int
)
