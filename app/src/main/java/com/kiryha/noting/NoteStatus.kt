package com.kiryha.noting

sealed class NoteStatus {
    object Edited : NoteStatus()
    object Saved : NoteStatus()
    object Deleted : NoteStatus()
    object Success : NoteStatus()
    object Failure : NoteStatus()
}