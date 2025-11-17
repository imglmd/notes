package com.kiryha.noting.domain.usecase.notes

import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.model.NoteListItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupNotesByMonthUseCase {
    operator fun invoke(notes: List<Note>): List<NoteListItem> {
        if (notes.isEmpty()) return emptyList()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val monthYearFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val monthDisplayFormat = SimpleDateFormat("LLLL", Locale("en"))

        val sortedNotes = notes.sortedByDescending {
            try {
                dateFormat.parse(it.date)?.time ?: 0
            } catch (e: Exception) {
                0L
            }
        }

        val groupedMap = sortedNotes.groupBy { note ->
            try {
                val date = dateFormat.parse(note.date)
                monthYearFormat.format(date ?: Date())
            } catch (e: Exception) {
                "unknown"
            }
        }

        val result = mutableListOf<NoteListItem>()
        groupedMap.forEach { (monthYear, notesInMonth) ->
            val displayMonth = try {
                val date = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(monthYear)
                monthDisplayFormat.format(date ?: Date()).replaceFirstChar { it.uppercase() }
            } catch (e: Exception) {
                "unknown"
            }

            if (notesInMonth.isNotEmpty()) {
                result.add(NoteListItem.MonthHeader(month = displayMonth, key = monthYear))
                notesInMonth.forEach { note ->
                    result.add(NoteListItem.NoteItem(note))
                }
            }
        }

        return result
    }
}