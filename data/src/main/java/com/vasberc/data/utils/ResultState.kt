package com.vasberc.data.utils

import com.vasberc.data.models.ErrorModel

sealed class ResultState <out T: Any> {
    data class Success<T: Any>(val data: T): ResultState<T>()
    data class Error(val error: ErrorModel): ResultState<Nothing>()

    data object Loading: ResultState<Nothing>()

    fun handle(
        onLoading: (() -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null,
        onError: ((ErrorModel) -> Unit)? = null
    ) {
        when(this) {
            is ResultState.Loading -> onLoading?.invoke()
            is ResultState.Error -> onError?.invoke(this.error)
            is ResultState.Success -> onSuccess?.invoke(this.data)
        }
    }
}