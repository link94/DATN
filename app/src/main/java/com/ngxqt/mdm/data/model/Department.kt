package com.ngxqt.mdm.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Department(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("contact") val contact: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("author_id") val authorId: Int?,
    @SerializedName("nursing_id") val nursingId: Int?,
    @SerializedName("image") val image: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("browser") val browser: String?,
    @SerializedName("browser_day") val browserDay: String?
): Parcelable
