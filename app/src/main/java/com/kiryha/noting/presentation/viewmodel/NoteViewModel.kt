package com.kiryha.noting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.data.repository.NoteRepository
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _notes = MutableStateFlow<ResultWithStatus<List<Note>>>(
        ResultWithStatus(
            emptyList(),
            NoteStatus.Success
        )
    )
    val notes: StateFlow<ResultWithStatus<List<Note>>> = searchText.combine(_notes) { text, result ->
        if (text.isBlank()) {
            result
        } else {
            ResultWithStatus(
                item = result.item.filter { it.doesMatchSearchQuery(text) },
                status = result.status
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _notes.value
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

    fun getNote(id: Int) {
        viewModelScope.launch {
            val result = repository.getNote(id)
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
            if (result.status == NoteStatus.Deleted) {
                loadNotes()
            }
            _status.value = result.status
        }
    }
}