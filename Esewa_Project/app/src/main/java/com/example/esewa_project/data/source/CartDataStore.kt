package com.example.esewa_project.data.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "cart_prefs")

class CartDataStore(private val context: Context) {
     private val CART_COUNT = intPreferencesKey("cart_item_count")

        val cartCount: Flow<Int> = context.dataStore.data.map{preferences->
            preferences[CART_COUNT]?:0
        }

        suspend fun updateCount(delta:Int){
            context.dataStore.edit { preferences ->
                val current = preferences[CART_COUNT] ?: 0
                val newCount = (current + delta).coerceAtLeast(0)
                preferences[CART_COUNT] = newCount
            }
        }
    }

