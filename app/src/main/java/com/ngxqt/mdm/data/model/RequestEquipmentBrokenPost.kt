package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class RequestEquipmentBrokenPost(
    @SerializedName("date_failure")
    val dateFailure: String,
    @SerializedName("reason")
    val reason: String
)
