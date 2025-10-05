package com.kiryha.noting.domain.model

sealed class NoteListItem {
    data class MonthHeader(val month: String, val key: String): NoteListItem()
    data class NoteItem(val note: Note) : NoteListItem()
}