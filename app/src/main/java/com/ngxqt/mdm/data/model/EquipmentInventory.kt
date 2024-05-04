package com.ngxqt.mdm.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EquipmentInventory(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("serial") val serial: String?,
    @SerializedName("inventories") val inventories: TransformerInventories?
): Parcelable {
    @Parcelize
    data class TransformerInventories(
        @SerializedName("note") val note: String?,
        @SerializedName("date") val date: String?,
        @SerializedName("equipment_id") val equipmentId: Int?
    ): Parcelable
}
