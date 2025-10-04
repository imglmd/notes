package com.kiryha.noting.domain.status

data class ResultWithStatus<T> (
    val item: T,
    val status: NoteStatus
)