package com.kiryha.noting.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsertNote(note: LocalNote): Long

    @Delete
    suspend fun deleteNote(note: LocalNote): Int

    @Query("SELECT * FROM note ORDER BY date DESC")
    suspend fun getNotes(): List<LocalNote>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): LocalNote?

}