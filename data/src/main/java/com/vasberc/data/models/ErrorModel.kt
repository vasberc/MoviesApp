package com.vasberc.data.models

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
