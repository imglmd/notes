package com.kiryha.noting.data

import com.kiryha.noting.data.source.local.LocalNote
import com.kiryha.noting.data.source.network.NetworkNote
import com.kiryha.noting.data.source.network.NetworkUser
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.model.User

// ============ note ============

fun Note.toLocal(userId: String?) = LocalNote(
    id = id,
    text = text,
    date = date,
    userId = userId,
    isPinned = isPinned,
    isSynced = false,
    isDeleted = false
)

fun List<Note>.toLocal(userId: String?) = map { it.toLocal(userId) }

fun LocalNote.toExternal() = Note(
    id = id,
    text = text,
    date = date,
    isPinned = isPinned
)

@JvmName("localToExternal")
fun List<LocalNote>.toExternal() = map(LocalNote::toExternal)

fun NetworkNote.toLocal() = LocalNote(
    id = app_id,
    text = text,
    date = date,
    userId = user_id,
    isPinned = is_pinned,
    isSynced = true,
    isDeleted = false
)

@JvmName("networkToLocal")
fun List<NetworkNote>.toLocal() = map(NetworkNote::toLocal)

fun LocalNote.toNetwork() = NetworkNote(
    app_id = id,
    user_id = userId ?: "",
    text = text,
    date = date,
    is_pinned = isPinned
)

fun List<LocalNote>.toNetwork() = map(LocalNote::toNetwork)

fun Note.toNetwork(userId: String) = NetworkNote(
    app_id = id,
    user_id = userId,
    text = text,
    date = date,
    is_pinned = isPinned
)

@JvmName("externalToNetwork")
fun List<Note>.toNetwork(userId: String) = map { it.toNetwork(userId) }

fun NetworkNote.toExternal() = Note(
    id = app_id,
    text = text,
    date = date,
    isPinned = is_pinned
)

@JvmName("networkToExternal")
fun List<NetworkNote>.toExternal() = map(NetworkNote::toExternal)

// ============ user ============

fun NetworkUser.toExternal() = User(
    id = id,
    email = email,
    username = username
)
