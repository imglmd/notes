package com.kiryha.noting.data.source.network

import kotlinx.serialization.Serializable

@Serializable
data class NetworkUser(
    val id: String,
    val email: String,
    val username: String
)