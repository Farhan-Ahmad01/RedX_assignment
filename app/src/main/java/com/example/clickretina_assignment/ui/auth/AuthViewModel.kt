package com.example.clickretina_assignment.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Authenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    object Unauthenticated : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = mutableStateOf<AuthState>(AuthState.Unauthenticated)
    val authState: State<AuthState> = _authState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, pass).await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
