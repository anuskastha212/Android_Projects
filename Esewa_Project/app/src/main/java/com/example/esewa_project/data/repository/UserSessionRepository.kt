package com.example.esewa_project.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.esewa_project.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")
class UserSessionRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private object PreferencesKeys{
        val UID = stringPreferencesKey("uid")
        val EMAIL = stringPreferencesKey("email")
        val NAME = stringPreferencesKey("name")
        val PHONE = stringPreferencesKey("phone")
        val PHOTO_URL = stringPreferencesKey("photo_url")
    }

    fun getUid(): String? = auth.currentUser?.uid

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            uid = prefs[PreferencesKeys.UID] ?: "",
            email = prefs[PreferencesKeys.EMAIL] ?: "",
            name = prefs[PreferencesKeys.NAME] ?: "",
            phone = prefs[PreferencesKeys.PHONE] ?: "",
            photoUrl = prefs[PreferencesKeys.PHOTO_URL] ?: ""
        )
    }

    val currentUserId: Flow<String?> = callbackFlow {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.uid)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun saveSession(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.UID] = profile.uid
            prefs[PreferencesKeys.EMAIL] = profile.email
            prefs[PreferencesKeys.NAME] = profile.name
            prefs[PreferencesKeys.PHONE] = profile.phone
            prefs[PreferencesKeys.PHOTO_URL] = profile.photoUrl
        }
    }
    suspend fun clearSession(){
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}