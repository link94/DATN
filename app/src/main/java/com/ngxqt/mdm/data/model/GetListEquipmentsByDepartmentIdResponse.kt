package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetListEquipmentsByDepartmentIdResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("data")
    var data: MutableList<EquipmentInventory>,
    @SerializedName("dataLength")
    var dataLength: Int
)
