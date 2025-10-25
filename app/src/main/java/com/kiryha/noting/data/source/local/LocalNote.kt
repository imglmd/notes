package com.kiryha.noting.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class LocalNote(
    @PrimaryKey
    val id: Int,
    val text: String,
    val date: String,
    val userId: String? = null,  // для связи с пользователем
    val isSynced: Boolean = false,  // флаг синхронизации
    val isDeleted: Boolean = false  // флаг для отложенного удаления
)