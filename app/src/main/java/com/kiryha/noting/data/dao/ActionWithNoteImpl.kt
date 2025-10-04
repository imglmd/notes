package com.kiryha.noting.data.dao

import com.kiryha.noting.domain.status.ResultWithStatus
import com.kiryha.noting.domain.model.Note

interface ActionWithNoteImpl {
    suspend fun upsertNote(note: Note): ResultWithStatus<List<Note>>
    suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>>
    suspend fun getNotes(): ResultWithStatus<List<Note>>
    suspend fun getNote(id: Int): ResultWithStatus<Note>
}