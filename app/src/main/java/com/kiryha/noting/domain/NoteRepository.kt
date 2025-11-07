package com.kiryha.noting.domain

import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.status.ResultWithStatus

interface NoteRepository {
    suspend fun upsertNote(note: Note):  ResultWithStatus<List<Note>>
    suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>>
    suspend fun getNotes(): ResultWithStatus<List<Note>>
    suspend fun getNoteById(id: Int): ResultWithStatus<Note>
    suspend fun fullSync(): ResultWithStatus<Unit>
    suspend fun clearLocalData()
}