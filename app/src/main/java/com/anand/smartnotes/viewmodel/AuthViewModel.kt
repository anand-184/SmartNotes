package com.anand.smartnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anand.smartnotes.MainActivity
import com.anand.smartnotes.data.dataclasses.User
import com.anand.smartnotes.data.repositories.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel:ViewModel() {
    private val authRepository = AuthRepository()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun getUser(): FirebaseUser?{
        return authRepository.getCurrentUser()
    }

    fun register(userEmail: String, password: String, confirmPassword:String,userName: String, university: String,
                 program: String, semester: String, batch: String)
    {
        if (userEmail.isEmpty() || password.isEmpty() || userName.isEmpty() || university.isEmpty()
            || program.isEmpty() || semester.isEmpty()) {
            _authState.value = AuthState.Error("Please fill all the fields")
            return
        }
        if(confirmPassword!=password){
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }
        if(password.length<6){
            _authState.value = AuthState.Error("Password should be at least 6 characters long")
            return
        }

        viewModelScope.launch {
            _authState.value=AuthState.Loading
            authRepository.registerUser(userEmail,password,userName,university,program,semester,batch).onSuccess {
                _authState.value=AuthState.Success("User Registered Successfully")
            }.onFailure {
                _authState.value=AuthState.Error(it.message?:"Unknown Error")
            }
        }
    }

    fun login(email: String,password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill all details")
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.loginUser(email, password).onSuccess {
                _authState.value = AuthState.Success("User Logged In Successfully")
            }
        }
    }

    fun resetState(){
        _authState.value=AuthState.Idle
    }






}