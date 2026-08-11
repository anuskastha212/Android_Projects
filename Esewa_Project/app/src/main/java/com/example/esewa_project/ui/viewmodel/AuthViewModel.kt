package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

    private val _authResult = MutableLiveData<Result<Unit>?>()
    val authResult: LiveData<Result<Unit>?> = _authResult

    private val _userData = MutableLiveData<Map<String, Any>?>()
    val userData: LiveData<Map<String, Any>?> = _userData

    private val _resetPassword = MutableLiveData<Result<Unit>?>()
    val resetPassword: LiveData<Result<Unit>?> = _resetPassword

    fun register(email: String, password: String, name: String, phone: String){
        viewModelScope.launch {
            _authResult.value = repository.registerUser(email, password, name, phone)
        }
    }

    fun login(email: String, password: String){
        viewModelScope.launch {
            _authResult.value = repository.loginUser(email, password)
        }
    }

    fun isUserLoggedIn() = repository.isUserLoggedIn()

    fun fetchUserDetails() {
        viewModelScope.launch {
            _userData.value = repository.getUserDetails()
        }
    }

    fun sendResetEmail(email: String){
        viewModelScope.launch{
            _resetPassword.value = repository.sendPasswordResetEmail(email)
        }
    }

    fun resetPasswordResult() {
        _resetPassword.value = null
    }

    fun logout() {
        repository.logout()
        _userData.value = null
    }

    fun resetResult(){
        _authResult.value = null
    }
}