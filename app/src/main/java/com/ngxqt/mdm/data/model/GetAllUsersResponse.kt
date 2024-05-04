package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetAllUsersResponse(
    @SerializedName("data")
    var data: MutableList<User>,
    @SerializedName("dataLength")
    var dataLength: Int
)
