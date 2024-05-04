package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetEquipmentsResponse(
    @SerializedName("status")
    var status: String,
    @SerializedName("data")
    var data: EquipmentsData,
    @SerializedName("dataLength")
    var dataLength: Int
){
    data class EquipmentsData(
        @SerializedName("current_page")
        val currentPage: Int?,
        @SerializedName("data")
        val data: MutableList<Equipment>,
        @SerializedName("first_page_url")
        val firstPageUrl: String?,
        @SerializedName("from")
        val from: Int?,
        @SerializedName("next_page_url")
        val nextPageUrl: String?,
        @SerializedName("path")
        val path: String?,
        @SerializedName("per_page")
        val perPage: String?,
        @SerializedName("prev_page_url")
        val prevPageUrl: String?,
        @SerializedName("to")
        val to: Int?
    )
}
