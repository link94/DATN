package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class RequestEquipmentBrokenResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("message")
    var message: String
)
