package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class RequestEquipmentInventoryResponse(
    @SerializedName("status_code")
    var status: Int,
    @SerializedName("message")
    var message: String
)
