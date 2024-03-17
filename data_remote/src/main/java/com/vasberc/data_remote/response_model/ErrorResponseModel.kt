package com.vasberc.data_remote.response_model


import com.google.gson.annotations.SerializedName

data class ErrorResponseModel(
    @SerializedName("status_code")
    val statusCode: Int?,
    @SerializedName("status_message")
    val statusMessage: String?,
    @SerializedName("success")
    val success: Boolean?
)