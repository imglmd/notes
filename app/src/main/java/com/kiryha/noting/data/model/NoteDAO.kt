package com.kiryha.noting.data.model

import com.kiryha.noting.ActionWithNoteImpl
import com.kiryha.noting.NoteStatus
import com.kiryha.noting.ResultWithStatus

class NoteDAO(): ActionWithNoteImpl {
    private val notes: MutableList<Note> = mutableListOf()

    override fun editNote(id: Int, note: Note): ResultWithStatus<List<Note>> {
        notes[id] = note
        return ResultWithStatus(notes, NoteStatus.Edited)
    }

    override fun deleteNote(id: Int): ResultWithStatus<List<Note>> {
        notes.removeAt(id)
        return ResultWithStatus(notes, NoteStatus.Deleted)
    }

    override fun saveTask(note: Note): ResultWithStatus<List<Note>> {
        notes.add(note)
        return ResultWithStatus(notes, NoteStatus.Saved)
    }

    override fun getNotes(): ResultWithStatus<List<Note>> {
        return ResultWithStatus(notes, NoteStatus.Success)
    }

    override fun getNote(id: Int): ResultWithStatus<Note> {
        val newNote = notes[id]
        return ResultWithStatus(newNote, NoteStatus.Success)
    }
}