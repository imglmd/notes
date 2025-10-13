package com.kiryha.noting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.data.AuthRepository
import com.kiryha.noting.data.NoteRepository
import com.kiryha.noting.domain.status.AuthStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authStatus: StateFlow<AuthStatus> = _authStatus

    fun signUp(email: String, username: String, password: String){
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
        }
    }
    fun signInByEmail(email: String, password: String){
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            authRepository.signInWithEmailAndPassword(email, password)


        }
    }
    fun signOut(){
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading

        }
    }
}