package com.spec.infrastructure.http

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MlSearchResponse(
    val results: List<MlSearchResult>
)

@Serializable
data class MlSearchResult(
    val id: String,
    val name: String,
    val pictures: List<MlPicture>? = null
)

@Serializable
data class MlProductDetail(
    val id: String,
    val name: String,
    val attributes: List<MlAttribute> = emptyList(),
    val pictures: List<MlPicture> = emptyList(),
    @SerialName("short_description")
    val shortDescription: MlShortDescription? = null
)

@Serializable
data class MlAttribute(
    val name: String,
    @SerialName("value_name")
    val valueName: String? = null
)

@Serializable
data class MlPicture(
    val url: String
)

@Serializable
data class MlShortDescription(
    val content: String? = null
)

@Serializable
data class MlTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int
)
