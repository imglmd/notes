package com.kiryha.noting.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class LocalNote(
    @PrimaryKey
    val id: Int = 0,
    val text: String,
    val date: String
)