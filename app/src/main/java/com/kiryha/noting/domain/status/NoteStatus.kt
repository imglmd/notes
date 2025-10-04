package com.kiryha.noting.domain.status

sealed class NoteStatus {
    object Success : NoteStatus()
    object Deleted : NoteStatus()
    object Failure : NoteStatus()
}