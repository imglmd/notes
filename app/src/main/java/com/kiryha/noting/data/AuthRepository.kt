package com.kiryha.noting.data

import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.model.User
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository(
    private val networkSource: NetworkDataSource
) {
    private val auth = networkSource.auth

    suspend fun signUpWithEmailAndPassword(email: String, password: String){
        try {
            auth.signUpWith(Email){
                this.email = email
                this.password = password
            }
        } catch (e: Exception){

        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String){
        auth.signInWith(Email){
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut(){
        try {
            auth.signOut()
        } catch (e: Exception){

        }
    }
    suspend fun isAuth(): Boolean{
        return auth.currentSessionOrNull() != null
    }
}