package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class StatisticalEquipmentsResponse(
    @SerializedName("equipments")
    var equipment: MutableList<Equipment>
)
