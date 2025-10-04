package com.kiryha.noting.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val date: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        if (query.isBlank()) return true
        return text.contains(query, ignoreCase = true) || date.contains(query, ignoreCase = true)
    }
}