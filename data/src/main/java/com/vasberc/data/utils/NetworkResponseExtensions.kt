package com.vasberc.data.utils

import com.haroldadmin.cnradapter.NetworkResponse
import com.vasberc.data.models.ErrorModel
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T : Any, U : Any, R : Any>NetworkResponse<T, U>.parseResponse(
    successMapper: T.() -> R,
    serverErrorMapper: U.(Int) -> ErrorModel.ServerError
): ResultState<R> =

    when (this) {
        is NetworkResponse.Success -> ResultState.Success(this.body.successMapper())
        is NetworkResponse.ServerError -> ResultState.Error(this.body!!.serverErrorMapper(this.code!!))
        is NetworkResponse.NetworkError -> ResultState.Error(getError(this.error))
        is NetworkResponse.UnknownError -> ResultState.Error(getError(this.error))
    }

private fun getError(throwable: Throwable): ErrorModel = when (throwable) {
    is ConnectException -> ErrorModel.NetworkError.Network
    is SocketTimeoutException -> ErrorModel.NetworkError.Timeout
    is UnknownHostException -> ErrorModel.NetworkError.ServerUnavailable
    is InterruptedIOException -> ErrorModel.NetworkError.Network
    is IOException -> ErrorModel.NetworkError.Network
    else -> ErrorModel.Unknown(throwable.message)
}