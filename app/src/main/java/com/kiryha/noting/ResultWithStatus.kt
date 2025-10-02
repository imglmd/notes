package com.kiryha.noting

data class ResultWithStatus<T> (
    val item: T,
    val status: NoteStatus
)