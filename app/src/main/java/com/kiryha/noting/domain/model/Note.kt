package com.kiryha.noting.domain.model

import java.text.SimpleDateFormat
import java.util.*

data class Note(
    val id: Int = 0,
    val text: String,
    val date: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        if (query.isBlank()) return true
        if (text.contains(query, ignoreCase = true)) return true
        if (date.contains(query, ignoreCase = true)) return true
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val monthDisplayFormat = SimpleDateFormat("LLLL", Locale("en"))
            val parsedDate = dateFormat.parse(date)
            val monthName = monthDisplayFormat.format(parsedDate ?: return false)

            if (monthName.contains(query, ignoreCase = true)) return true
        } catch (e: Exception) { }

        return false
    }
}