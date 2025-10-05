package com.kiryha.noting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.data.repository.NoteRepository
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.model.NoteListItem
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
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
            val testNotes = listOf(
                // 2025 - Октябрь
                Note(text = "Купить продукты:\n• Молоко\n• Хлеб\n• Яйца\n• Сыр\n• Овощи\n• Фрукты", date = "2025-10-05"),
                Note(text = "Встреча с клиентом в 15:00 по проекту. Подготовить презентацию и договор.", date = "2025-10-04"),
                Note(text = "Идеи", date = "2025-10-03"),
                Note(text = "Позвонить врачу и записаться на прием. Не забыть взять медицинскую карту и результаты анализов.", date = "2025-10-02"),
                Note(text = "Kotlin: изучить Coroutines и Flow. Посмотреть документацию и примеры использования в реальных проектах.", date = "2025-10-01"),

                // 2025 - Сентябрь
                Note(text = "Отпуск с 15 по 30 сентября! Забронировать отель в Сочи, купить билеты на самолет, оформить страховку.", date = "2025-09-28"),
                Note(text = "Список книг:\n1. 1984 - Оруэлл\n2. Мастер и Маргарита\n3. Преступление и наказание\n4. Граф Монте-Кристо\n5. Великий Гэтсби", date = "2025-09-25"),
                Note(text = "🎂 Мама", date = "2025-09-20"),
                Note(text = "План тренировок на неделю:\nПн - Грудь, трицепс\nСр - Спина, бицепс\nПт - Ноги, плечи\nВс - Кардио", date = "2025-09-15"),
                Note(text = "Коммуналка до 10 числа", date = "2025-09-05"),
                Note(text = "Пароль от Wi-Fi для гостей: GuestNetwork2025", date = "2025-09-01"),

                // 2025 - Август
                Note(text = "Ремонт:\n✓ Покрасить стены в гостиной\n✓ Заменить люстру в спальне\n○ Купить новый диван\n○ Постелить ламинат\n○ Установить кондиционер", date = "2025-08-28"),
                Note(text = "Android Dev Summit 20 августа в 10:00. Темы: Jetpack Compose, Material 3, Performance optimization", date = "2025-08-15"),
                Note(text = "Изучить новые фичи Jetpack Compose", date = "2025-08-10"),
                Note(text = "Дача: купить семена, удобрения, инструменты. Починить забор.", date = "2025-08-05"),

                // 2025 - Июль
                Note(text = "Отчет по проекту NotesApp:\n✓ Архитектура MVVM - 100%\n✓ UI в Compose - 100%\n✓ База данных Room - 100%\n○ Тестирование - 60%\n○ Оптимизация - 40%\nРелиз: сентябрь 2025", date = "2025-07-30"),
                Note(text = "Море! 🌊", date = "2025-07-20"),
                Note(text = "Новый рецепт пасты карбонара с беконом и пармезаном - обязательно попробовать на выходных!", date = "2025-07-15"),
                Note(text = "Backup всех проектов на внешний диск", date = "2025-07-05"),

                // 2025 - Июнь
                Note(text = "Идея стартапа: AI-powered приложение для учета личных финансов с автоматической категоризацией трат и предсказанием бюджета", date = "2025-06-25"),
                Note(text = "Подарок Алексею на ДР", date = "2025-06-20"),
                Note(text = "Обновить резюме: добавить последние проекты, навыки Kotlin, Compose, обновить LinkedIn профиль", date = "2025-06-10"),
                Note(text = "Английский: практика speaking каждый день по 30 минут", date = "2025-06-05"),

                // 2025 - Май
                Note(text = "Весенняя генеральная уборка:\n□ Помыть все окна\n□ Разобрать гардероб\n□ Почистить балкон\n□ Выбросить старые вещи\n□ Постирать шторы", date = "2025-05-28"),
                Note(text = "Посадить на даче: помидоры, огурцы, перец, зелень, цветы", date = "2025-05-15"),
                Note(text = "Важная встреча с инвесторами! Подготовить питч-презентацию и финансовую модель.", date = "2025-05-05"),

                // 2024 - Декабрь
                Note(text = "Новый год! Купить подарки всем, составить меню, украсить квартиру", date = "2024-12-28"),
                Note(text = "Подвести итоги года:\n- Выучил Kotlin ✓\n- Создал 3 проекта ✓\n- Нашел работу ✓\n- Начал заниматься спортом ✓", date = "2024-12-20"),
                Note(text = "Зимние шины", date = "2024-12-10"),

                // 2024 - Октябрь
                Note(text = "Старая заметка: переезд в новую квартиру завершен. Осталось только расставить мебель.", date = "2024-10-15"),
                Note(text = "Октябрь 2024: начал изучать Android разработку", date = "2024-10-10"),
                Note(text = "Первая заметка в приложении!", date = "2024-10-01"),

                // 2024 - Июль
                Note(text = "Отпуск в Турции был невероятным! Море, солнце, отличная кухня. Обязательно вернуться.", date = "2024-07-25"),
                Note(text = "Закончил онлайн-курс по Kotlin. Получил сертификат!", date = "2024-07-10"),

                // 2023 - Декабрь
                Note(text = "Цели на 2024:\n1. Выучить Kotlin и Android\n2. Создать своё приложение\n3. Найти работу разработчиком\n4. Начать здоровый образ жизни\n5. Читать минимум 1 книгу в месяц", date = "2023-12-31"),
                Note(text = "Рождество с семьей", date = "2023-12-25"),

                // 2023 - Июнь
                Note(text = "Решил стать Android разработчиком. Начинаю с основ Java.", date = "2023-06-15"),
                Note(text = "Летние каникулы", date = "2023-06-01"),

                // 2023 - Январь
                Note(text = "Новый год, новая жизнь! Время перемен.", date = "2023-01-01")
            )

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