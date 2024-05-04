package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String?,
    @SerializedName("current_team_id")
    val currentTeamId: Any?,
    @SerializedName("displayname")
    val displayName: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("department_id")
    val departmentId: Int?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("is_disabled")
    val isDisabled: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String?
)
