package com.kiryha.noting.domain.status

data class ResultWithStatus<T, S>(
    val item: T,
    val status: S
)