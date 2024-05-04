package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetListInventoryResponse(
    @SerializedName("status_code")
    var status: Int,
    @SerializedName("data")
    var data: MutableList<Inventory>,
    @SerializedName("dataLength")
    var dataLength: Int
)
