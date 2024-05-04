package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class SearchEquipmentsByIdResponse (
    @SerializedName("data")
    val data: Equipment
)