package com.kiryha.noting.domain

import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus

interface NoteRepository {
    suspend fun upsertNote(note: Note):  ResultWithStatus<List<Note>, NoteStatus>
    suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>, NoteStatus>
    suspend fun getNotes(): ResultWithStatus<List<Note>, NoteStatus>
    suspend fun getNoteById(id: Int): ResultWithStatus<Note, NoteStatus>
    suspend fun fullSync(): ResultWithStatus<Unit, NoteStatus>
    suspend fun clearLocalData()
}