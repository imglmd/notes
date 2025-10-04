package com.kiryha.noting.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiryha.noting.data.dao.NoteDao
import com.kiryha.noting.domain.model.Note

@Database(
    entities = [Note::class],
    version = 1
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val noteDao: NoteDao
}