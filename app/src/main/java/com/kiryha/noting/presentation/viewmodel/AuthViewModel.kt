package com.kiryha.noting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.data.AuthRepository
import com.kiryha.noting.data.NoteRepository
import com.kiryha.noting.domain.status.AuthStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authStatus: StateFlow<AuthStatus> = _authStatus

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password


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

    fun onUsernameChange(newText: String){
        _username.value = newText
    }
    fun onEmailChange(newText: String){
        _email.value = newText
    }
    fun onPasswordChange(newText: String){
        _password.value = newText
    }

}