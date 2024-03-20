package com.vasberc.data.models

import com.vasberc.data_remote.response_model.ErrorResponseModel

sealed class ErrorModel {

    //Connectivity errors
    sealed class NetworkError : ErrorModel() {
        data object Network : NetworkError()
        data object Timeout : NetworkError()
        data object ServerUnavailable : NetworkError()
    }

    //HTTP Error 400+
    data class ServerError(
        val type: Int,
        val message: String,
        val status: Int,
    ) : ErrorModel()

    data class Unknown(val throwableString: String? = null) : ErrorModel()
}

fun ErrorResponseModel.asErrorModel(status: Int): ErrorModel.ServerError {
    return ErrorModel.ServerError(
        type = statusCode ?: -1,
        message = statusMessage ?: "",
        status = status
    )
}
