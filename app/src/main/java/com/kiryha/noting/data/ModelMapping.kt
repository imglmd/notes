package com.kiryha.noting.data

import com.kiryha.noting.data.source.local.LocalNote
import com.kiryha.noting.domain.model.Note

fun Note.toLocal() = LocalNote(
    id = id,
    text = text,
    date = date
)
fun List<Note>.toLocal() = map(Note::toLocal)


fun LocalNote.toExternal() = Note(
    id = id,
    text = text,
    date = date
)

@JvmName("localToExternal")
fun List<LocalNote>.toExternal() = map(LocalNote::toExternal)

