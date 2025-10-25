package com.kiryha.noting.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_notes")
data class DeletedNote(
    @PrimaryKey
    val id: Int,
    val deletedAt: Long = System.currentTimeMillis()
)