package com.joshgm3z.netplayer.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DatastoreWrapper
@Inject
constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val SESSION_ID = stringPreferencesKey("session_id")
    }

    suspend fun setSessionId(sessionId: String) {
        dataStore.edit { preferences ->
            preferences[SESSION_ID] = sessionId
        }
    }

    suspend fun getSessionId() = dataStore.data.firstOrNull()?.let {
        it[SESSION_ID] ?: return null
    }

    suspend fun clearAllData() {
        dataStore.edit { it.clear() }
    }
}
