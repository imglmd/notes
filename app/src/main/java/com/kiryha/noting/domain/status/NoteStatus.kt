package com.kiryha.noting.domain.status

sealed class NoteStatus {
    object Success : NoteStatus()
    object Deleted : NoteStatus()
    data class Failure(val message: String? =  null): NoteStatus()
}
