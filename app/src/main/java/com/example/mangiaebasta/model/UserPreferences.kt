package com.example.mangiaebasta.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object UserPreferences {
    private val SID_KEY = stringPreferencesKey("sid")
    private val UID_KEY = intPreferencesKey("uid")

    suspend fun saveUser(context: Context, sid: String, uid: Int) {
        context.dataStore.edit { prefs ->
            prefs[SID_KEY] = sid
            prefs[UID_KEY] = uid
        }
    }

    suspend fun getUser(context: Context): UserResponse? {
        val prefs = context.dataStore.data.first()
        val sid = prefs[SID_KEY]
        val uid = prefs[UID_KEY]
        return if (sid != null && uid != null) {
            UserResponse(sid, uid)
        } else {
            null
        }
    }

}
