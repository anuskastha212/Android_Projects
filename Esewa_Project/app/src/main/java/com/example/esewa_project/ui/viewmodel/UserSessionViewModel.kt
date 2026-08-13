package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.esewa_project.data.repository.UserSessionRepository

class UserSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserSessionRepository(application)

    val userProfile = repository.userProfile.asLiveData()
    val currentUserId = repository.currentUserId.asLiveData()
}