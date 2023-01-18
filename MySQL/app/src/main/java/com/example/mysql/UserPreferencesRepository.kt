package com.example.mysql

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferencesDataStore")
        val intPreferencesKey = intPreferencesKey("intPreferencesKey")
    }

    suspend fun setValue(value: Int) {
        dataStore.edit {
            it[intPreferencesKey] = value
        }
    }

    suspend fun getValue(): Int? {
        return dataStore.data.first()[intPreferencesKey]
    }

    fun getValues(): Flow<Int> {
        return dataStore.data
            .catch {
                if (it is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map {
                val intValue = it[intPreferencesKey] ?: -1
                intValue
            }
    }
}