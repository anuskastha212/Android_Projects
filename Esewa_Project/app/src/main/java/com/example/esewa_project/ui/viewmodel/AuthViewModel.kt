package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.model.UserProfile
import com.example.esewa_project.data.repository.AuthRepository
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.FavouriteRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val userSessionRepository = UserSessionRepository(application)

    private val _authResult = MutableLiveData<Result<Unit>?>()
    val authResult: LiveData<Result<Unit>?> = _authResult

    private val _userData = MutableLiveData<Map<String, Any>?>()
    val userData: LiveData<Map<String, Any>?> = _userData

    private val _resetPassword = MutableLiveData<Result<Unit>?>()
    val resetPassword: LiveData<Result<Unit>?> = _resetPassword
    val localUserData: LiveData<UserProfile> = userSessionRepository.userProfile.asLiveData()


    fun register(email: String, password: String, name: String, phone: String){
        viewModelScope.launch {
            _authResult.value = authRepository.registerUser(email, password, name, phone)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            _authResult.value = result
            if (result.isSuccess) {
                val uid =  authRepository.getCurrentUserUid() ?: ""
                val db = AppDatabase.getDatabase(getApplication())
                val cartRepo = CartRepository(db.cartDao())
                val favRepo = FavouriteRepository(db.favouriteDao())
                cartRepo.syncCartFromCloud(uid)
                favRepo.syncFavouritesFromCloud(uid)
            }

        }
    }

    private suspend fun saveSessionLocally(email: String, name: String, phone: String) {
        val uid = authRepository.getCurrentUserUid() ?: ""
        val profile = UserProfile(
            uid = uid,
            email = email,
            name = name,
            phone = phone
        )
        userSessionRepository.saveSession(profile)
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            val details = authRepository.getUserDetails()
            _userData.value = details
            details?.let {
                saveSessionLocally(
                    email = it["email"] as? String ?: "",
                    name = it["name"] as? String ?: "",
                    phone = it["phone"] as? String ?: ""
                )
            }
        }
    }

    fun isUserLoggedIn() = authRepository.isUserLoggedIn()

    fun sendResetEmail(email: String){
        viewModelScope.launch{
            _resetPassword.value = authRepository.sendPasswordResetEmail(email)
        }
    }

    fun resetPasswordResult() {
        _resetPassword.value = null
    }

    fun logout() {
        authRepository.logout()
        viewModelScope.launch {
            userSessionRepository.clearSession()
        }
        _userData.value = null
    }

    fun resetResult(){
        _authResult.value = null
    }
}