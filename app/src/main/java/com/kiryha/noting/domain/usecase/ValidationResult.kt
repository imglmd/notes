package com.kiryha.noting.domain.usecase

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)