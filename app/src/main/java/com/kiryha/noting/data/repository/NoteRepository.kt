package com.kiryha.noting.data.repository

import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import com.kiryha.noting.data.dao.NoteDao
import com.kiryha.noting.data.dao.ActionWithNoteImpl
import com.kiryha.noting.domain.model.Note

class NoteRepository(private val noteDao: NoteDao) : ActionWithNoteImpl {
    override suspend fun upsertNote(note: Note): ResultWithStatus<List<Note>> {
        noteDao.upsertNote(note)
        return ResultWithStatus(noteDao.getNotes(), NoteStatus.Success)
    }

    override suspend fun deleteNote(id: Int): ResultWithStatus<List<Note>> {
        val note = noteDao.getNote(id) ?: return ResultWithStatus(emptyList(), NoteStatus.Failure)
        noteDao.deleteNote(note)
        return ResultWithStatus(noteDao.getNotes(), NoteStatus.Deleted)
    }

    override suspend fun getNotes(): ResultWithStatus<List<Note>> {
        return ResultWithStatus(noteDao.getNotes(), NoteStatus.Success)
    }

    override suspend fun getNote(id: Int): ResultWithStatus<Note> {
        val note = noteDao.getNote(id)
        return if (note != null) {
            ResultWithStatus(note, NoteStatus.Success)
        } else {
            ResultWithStatus(Note(id = 0, text = "", date = ""), NoteStatus.Failure)
        }
    }
}