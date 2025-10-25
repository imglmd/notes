package com.kiryha.noting.data.source.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class NetworkDataSource(private val supabase: SupabaseClient) {
    private val notesTable = supabase.from("notes")
    private val usersTable = supabase.from("users")
    val auth = supabase.auth


    // ======== user =========

    suspend fun getCurrentUser(): NetworkUser? {
        val userId = auth.currentUserOrNull()?.id ?: return null
        return try {
            usersTable.select {
                filter { eq("id", userId) }
            }.decodeSingle<NetworkUser>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun isEmailExists(email: String): Boolean {
        return try {
            val result = usersTable
                .select(columns = Columns.list("email")) {
                    filter { eq("email", email) }
                }
                .decodeList<Map<String, String>>()
            result.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isUsernameExists(username: String): Boolean {
        return try {
            val result = usersTable
                .select(columns = Columns.list("username")) {
                    filter { eq("username", username) }
                }
                .decodeList<Map<String, String>>()
            result.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }


    // ======== note =========

    suspend fun upsertNote(note: NetworkNote) {
        val userId = auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not authenticated")

        val noteWithUser = note.copy(user_id = userId)
        notesTable.upsert(noteWithUser)
    }

    suspend fun deleteNoteById(appId: Int) {
        val userId = auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not authenticated")

        notesTable.delete {
            filter {
                eq("app_id", appId)
                eq("user_id", userId)
            }
        }
    }

    suspend fun getNotesByUserId(): List<NetworkNote> {
        val userId = auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not authenticated")

        return notesTable.select {
            filter { eq("user_id", userId) }
        }.decodeList<NetworkNote>()
    }

    suspend fun getNoteByAppId(appId: Int): NetworkNote? {
        val userId = auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not authenticated")

        return try {
            notesTable.select {
                filter {
                    eq("app_id", appId)
                    eq("user_id", userId)
                }
            }.decodeSingle<NetworkNote>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteAllNotesByUserId() {
        val userId = auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not authenticated")

        notesTable.delete {
            filter { eq("user_id", userId) }
        }
    }
}