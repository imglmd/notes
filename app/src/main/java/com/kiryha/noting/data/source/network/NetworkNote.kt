package com.kiryha.noting.data.source.network

import kotlinx.serialization.Serializable

@Serializable
data class NetworkNote(
    val id: Int,
    val text: String,
    val date: String
)
