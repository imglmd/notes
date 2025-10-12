package com.kiryha.noting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.data.NoteRepository
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.model.NoteListItem
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import com.kiryha.noting.presentation.TestData.testNotes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _notes = MutableStateFlow<ResultWithStatus<List<Note>>>(
        ResultWithStatus(
            emptyList(),
            NoteStatus.Success
        )
    )
    val groupedNotes: StateFlow<ResultWithStatus<List<NoteListItem>>> = searchText.combine(_notes) { text, result ->
        val filteredNotes = if (text.isBlank()){
            result.item
        } else {
            result.item.filter { it.doesMatchSearchQuery(text) }
        }
        val groupedItems = groupNotesByMonth(filteredNotes)
        ResultWithStatus(
            item = groupedItems,
            status = result.status
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ResultWithStatus(emptyList(), NoteStatus.Success)
    )

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _status = MutableStateFlow<NoteStatus>(NoteStatus.Success)
    val status: StateFlow<NoteStatus> get() = _status.asStateFlow()

    private val _selectedNote = MutableStateFlow<ResultWithStatus<Note>>(
        ResultWithStatus(
            Note(id = -1, text = "", date = ""),
            NoteStatus.Success
        )
    )
    val selectedNote: StateFlow<ResultWithStatus<Note>> get() = _selectedNote.asStateFlow()

    init {
        loadNotes()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        _isSearching.value = text.isNotBlank()
    }

    fun loadNotes() {
        viewModelScope.launch {
            val result = repository.getNotes()
            _notes.value = result
            _status.value = result.status
        }
    }

    fun getNoteById(id: Int) {
        viewModelScope.launch {
            val result = repository.getNoteById(id)
            _selectedNote.value = result
            _status.value = result.status
        }
    }

    fun upsertNote(note: Note) {
        viewModelScope.launch {
            val result = repository.upsertNote(note)
            if (result.status == NoteStatus.Success) {
                loadNotes()
            }
            _status.value = result.status
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            val result = repository.deleteNote(id)
            if (result.status == NoteStatus.Success) {
                loadNotes()
            }
            _status.value = result.status
        }
    }

    private fun groupNotesByMonth(notes: List<Note>): List<NoteListItem> {
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

    fun addTestNotes() {
        viewModelScope.launch {
            testNotes.forEach { note ->
                repository.upsertNote(note)
            }

            loadNotes()
        }
    }

    fun clearAllNotes() {
        viewModelScope.launch {
            val currentNotes = repository.getNotes()

            currentNotes.item.forEach { note ->
                repository.deleteNote(note.id)
            }
            loadNotes()
        }
    }


}