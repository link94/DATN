package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class Inventory(
    @SerializedName("equipment_id") val equipmentId: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("note") val note: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("serial") val serial: String?
)
