package com.vasberc.data.models

import com.vasberc.data_remote.response_model.ConfigurationResponse

data class ImagesConfiguration(
    val backdropSizes: List<String>,
    val baseUrl: String,
    val logoSizes: List<String>,
    val posterSizes: List<String>,
    val profileSizes: List<String>,
    val secureBaseUrl: String,
    val stillSizes: List<String>
)

fun ConfigurationResponse.asImagesConfiguration(): ImagesConfiguration {
    return ImagesConfiguration(
        backdropSizes = images?.backdropSizes?.filterNotNull() ?: listOf(),
        baseUrl = images?.baseUrl ?: "",
        logoSizes = images?.logoSizes?.filterNotNull() ?: listOf(),
        posterSizes = images?.posterSizes?.filterNotNull() ?: listOf(),
        profileSizes = images?.profileSizes?.filterNotNull() ?: listOf(),
        secureBaseUrl = images?.secureBaseUrl ?: "",
        stillSizes = images?.stillSizes?.filterNotNull() ?: listOf()
    )
}