package com.kiryha.noting

import com.kiryha.noting.data.model.Note

interface ActionWithNoteImpl {
    fun editNote(id: Int, note: Note): ResultWithStatus<List<Note>>
    fun deleteNote(id: Int): ResultWithStatus<List<Note>>
    fun saveTask(note: Note): ResultWithStatus<List<Note>>
    fun getNotes(): ResultWithStatus<List<Note>>
    fun getNote(id: Int): ResultWithStatus<Note>
}