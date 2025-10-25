package com.kiryha.noting.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY date DESC")
    suspend fun getNotes(): List<LocalNote>

    @Query("SELECT * FROM notes WHERE id = :id AND isDeleted = 0")
    suspend fun getNoteById(id: Int): LocalNote?

    @Upsert
    suspend fun upsertNote(note: LocalNote)

    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :id")
    suspend fun markAsDeleted(id: Int)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("SELECT * FROM notes WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedNotes(): List<LocalNote>

    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    suspend fun getDeletedNotes(): List<LocalNote>

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun clearDeleted()

    @Query("DELETE FROM notes")
    suspend fun clearAll()
}


@Dao
interface DeletedNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deletedNote: DeletedNote)

    @Query("SELECT * FROM deleted_notes")
    suspend fun getAll(): List<DeletedNote>

    @Query("DELETE FROM deleted_notes WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM deleted_notes")
    suspend fun clearAll()
}
