package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val user: User,
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String
)
