package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class RequestEquipmentInventoryPost(
    @SerializedName("date")
    val date: String,
    @SerializedName("note")
    val note: String
)
