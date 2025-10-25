package com.kiryha.noting.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiryha.noting.data.source.local.NoteDao
import com.kiryha.noting.domain.model.Note

@Database(
    entities = [LocalNote::class, DeletedNote::class],
    version = 1,
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val deletedNoteDao: DeletedNoteDao
}