package com.example.esewa_project.data.source

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class LocalDataStore(private val context: Context) {
    private val CART_COUNT = intPreferencesKey("cart_count")
    private val CART_DATA = stringPreferencesKey("cart_data")
    private val FAVOURITE_IDS = stringSetPreferencesKey("favourite_ids")
    private val FAVOURITE_COUNT = intPreferencesKey("favourite_count")

    val cartCount: Flow<Int> = context.dataStore.data.map { it[CART_COUNT] ?: 0 }
    val favouriteCount: Flow<Int> = context.dataStore.data.map { it[FAVOURITE_COUNT] ?: 0 }
    val favouriteIds: Flow<Set<String>> = context.dataStore.data.map { it[FAVOURITE_IDS] ?: emptySet() }

    suspend fun updateCount(delta: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[CART_COUNT] ?: 0
            prefs[CART_COUNT] = (current + delta).coerceAtLeast(0)
        }
    }

    suspend fun saveCart(cartMap:Map<Int, Int>){
        val json = Gson().toJson(cartMap)
        context.dataStore.edit {
            it[CART_DATA]=json
        }
    }
    val cartMap: Flow<Map<Int, Int>> = context.dataStore.data.map { prefs ->
        val json = prefs[CART_DATA]
        if (json.isNullOrEmpty()) {
            emptyMap()
        } else {
            val type = object : com.google.gson.reflect.TypeToken<Map<Int, Int>>() {}.type
            Gson().fromJson(json, type)
        }
    }

    suspend fun toggleFavourite(productId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVOURITE_IDS] ?: emptySet()
            val idStr = productId.toString()
            val isAdding = !current.contains(idStr)
            prefs[FAVOURITE_IDS] = if (isAdding) current + idStr else current - idStr

            val currentCount = prefs[FAVOURITE_COUNT] ?: 0
            if (isAdding) {
                prefs[FAVOURITE_COUNT] = currentCount + 1
            } else {
                prefs[FAVOURITE_COUNT] = (currentCount - 1).coerceAtLeast(0)
            }
        }
    }
}