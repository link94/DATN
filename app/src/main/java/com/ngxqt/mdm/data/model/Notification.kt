package com.ngxqt.mdm.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("notifiable_id") val notifiableId: Int?,
    @SerializedName("notifiable_type") val notifiableType: String?,
    @SerializedName("data") val data: Data?,
    @SerializedName("read_at") val readAt: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
): Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("id") val id: Int?,
        @SerializedName("user_id") val userId: Int?,
        @SerializedName("content") val content: String?
    ): Parcelable
}
