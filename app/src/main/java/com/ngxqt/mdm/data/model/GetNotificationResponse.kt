package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetNotificationResponse(
    @SerializedName("status")
    var status: Int,
    @SerializedName("data")
    var data: MutableList<Notification>,
    @SerializedName("total")
    var total: Int
)
