package com.kiryha.noting.data

import com.kiryha.noting.data.source.local.LocalNote
import com.kiryha.noting.data.source.network.NetworkNote
import com.kiryha.noting.domain.model.Note

fun Note.toLocal() = LocalNote(
    id = id,
    text = text,
    date = date,
)
fun List<Note>.toLocal() = map(Note::toLocal)


fun LocalNote.toExternal() = Note(
    id = id,
    text = text,
    date = date
)

@JvmName("localToExternal")
fun List<LocalNote>.toExternal() = map(LocalNote::toExternal)

fun NetworkNote.toLocal() = LocalNote(
    id = id,
    text = text,
    date = date
)

@JvmName("networkToLocal")
fun List<NetworkNote>.toLocal() = map(NetworkNote::toLocal)

// Local to Network
fun LocalNote.toNetwork() = NetworkNote(
    id = id,
    text = text,
    date = date,
)

fun List<LocalNote>.toNetwork() = map(LocalNote::toNetwork)

// External to Network
fun Note.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Note>.toNetwork() = map(Note::toNetwork)

// Network to External
fun NetworkNote.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<NetworkNote>.toExternal() = map(NetworkNote::toExternal)