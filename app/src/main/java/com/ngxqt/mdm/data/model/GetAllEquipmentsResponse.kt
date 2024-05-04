package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetAllEquipmentsResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("data")
    var data: MutableList<Equipment>,
    @SerializedName("dataLength")
    var dataLength: Int
)
