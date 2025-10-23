package com.kiryha.noting.domain.status

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)