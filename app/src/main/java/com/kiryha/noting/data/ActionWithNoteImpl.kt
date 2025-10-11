package com.kiryha.noting.data

import com.kiryha.noting.data.source.local.LocalNote
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.status.ResultWithStatus

interface ActionWithNoteImpl {
    suspend fun upsertNote(note: Note): ResultWithStatus<List<Note>>
    suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>>
    suspend fun getNotes(): ResultWithStatus<List<Note>>
    suspend fun getNote(id: Int): ResultWithStatus<Note>
}