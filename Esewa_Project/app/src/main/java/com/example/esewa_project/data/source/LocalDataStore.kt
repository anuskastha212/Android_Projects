package com.example.esewa_project.data.source

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class LocalDataStore(private val context: Context) {
    private val CART_COUNT = intPreferencesKey("cart_count")
    private val FAVOURITE_IDS = stringSetPreferencesKey("favourite_ids")

    val cartCount: Flow<Int> = context.dataStore.data.map { it[CART_COUNT] ?: 0 }
    val favouriteIds: Flow<Set<String>> = context.dataStore.data.map { it[FAVOURITE_IDS] ?: emptySet() }

    suspend fun updateCount(delta: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[CART_COUNT] ?: 0
            prefs[CART_COUNT] = (current + delta).coerceAtLeast(0)
        }
    }

    suspend fun toggleFavourite(productId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVOURITE_IDS] ?: emptySet()
            val idStr = productId.toString()
            prefs[FAVOURITE_IDS] = if (current.contains(idStr)) current - idStr else current + idStr
        }
    }
}