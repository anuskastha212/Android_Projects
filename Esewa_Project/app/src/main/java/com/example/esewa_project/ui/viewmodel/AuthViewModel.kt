package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.UserProfile
import com.example.esewa_project.data.repository.AuthRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val repository = AuthRepository()
    private val userSessionRepository = UserSessionRepository(application)

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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _authResult.value = result
            if (result.isSuccess) {
                val details = repository.getUserDetails()
                details?.let {
                    saveSessionLocally(
                        email = it["email"] as? String ?: email,
                        name = it["name"] as? String ?: "",
                        phone = it["phone"] as? String ?: ""
                    )
                }
            }
        }
    }

    private suspend fun saveSessionLocally(email: String, name: String, phone: String) {
        val uid = repository.getCurrentUserUid() ?: ""
        val profile = UserProfile(
            uid = uid,
            email = email,
            name = name,
            phone = phone
        )
        userSessionRepository.saveSession(profile)
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