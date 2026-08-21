package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.local.entity.FavouriteEntity
import com.example.esewa_project.data.repository.FavouriteRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.esewa_project.data.local.entity.ProductEntity

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteViewModel(application: Application) : AndroidViewModel(application) {
    private val favRepo = FavouriteRepository(AppDatabase.getDatabase(application).favouriteDao())
    private val sessionRepo = UserSessionRepository(application)

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin = _navigateToLogin.asSharedFlow()

    val userSession: StateFlow<String> = sessionRepo.currentUserId
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, sessionRepo.getUid() ?: "")

    val favouriteCount: Flow<Int> = userSession.flatMapLatest { uid ->
        if (uid.isEmpty()) flowOf(0)
        else favRepo.getFavouriteCount(uid)
    }

    val favouriteIds: StateFlow<Set<Int>> = userSession.flatMapLatest { uid ->
        if (uid.isEmpty()) flowOf(emptySet())
        else favRepo.getFavouriteIds(uid).map { it.toSet() }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val favouriteProducts: StateFlow<List<ProductEntity>?> = userSession.flatMapLatest { uid ->
        if (uid.isEmpty()) flowOf(emptyList())
        else favRepo.getFavouriteProducts(uid)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun toggleFavourite(productId: Int) {
        val uid = userSession.value
        if (uid.isEmpty()) { viewModelScope.launch { _navigateToLogin.emit(Unit) }; return }
        viewModelScope.launch {
            if (favouriteIds.value.contains(productId)) favRepo.removeFavourite(uid, productId)
            else favRepo.addFavourite(FavouriteEntity(uid, productId, System.currentTimeMillis()))
        }
    }

    fun clearAllFavourites(){
        val uid = userSession.value
        if (uid.isEmpty()) return
        viewModelScope.launch {
            favRepo.clearAllFavourites(uid)
        }
    }

    fun removeFavourite(productId: Int) {
        val uid = userSession.value
        if (uid.isEmpty()) return
        viewModelScope.launch {
            favRepo.removeFavourite(uid, productId)
        }
    }
}