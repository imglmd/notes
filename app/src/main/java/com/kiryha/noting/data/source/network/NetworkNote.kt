package com.kiryha.noting.data.source.network

import kotlinx.serialization.Serializable

@Serializable
data class NetworkNote(
    val id: String? = null,  // UUID от Supabase
    val app_id: Int,
    val user_id: String,
    val text: String,
    val date: String,
    val created_at: String? = null,
    val updated_at: String? = null
)