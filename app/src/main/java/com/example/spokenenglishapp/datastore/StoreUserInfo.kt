package com.example.spokenenglishapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class StoreUserInfo(private val context: Context) {

    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserText")
        val USER_TEXT_KEY = stringPreferencesKey("user_text")
    }

    val getText: Flow<String?> = context.dataStore.data
        .map {preferences ->
            preferences[USER_TEXT_KEY] ?: ""
        }

    suspend fun saveText(name: String){
        context.dataStore.edit { preferences ->
            preferences[USER_TEXT_KEY] = name
        }
    }
}