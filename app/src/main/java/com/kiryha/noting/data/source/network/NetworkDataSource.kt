package com.kiryha.noting.data.source.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class NetworkDataSource(private val supabase: SupabaseClient){
    private val table = supabase.from("notes")
    val auth = supabase.auth

    suspend fun upsertNote(note: NetworkNote) = table.upsert(note)

    suspend fun deleteNoteById(id: Int) = table.delete{
        filter { eq("id", id) }
    }

    suspend fun getNotesByUserId(userId: Int): List<NetworkNote> =
        table.select {
            filter { eq("user_id", userId) }
        }.decodeList<NetworkNote>()


    suspend fun getNoteById(id: Int): NetworkNote =
        table.select{
            filter {
                eq("id", id)
            }
        }.decodeSingle<NetworkNote>()
}