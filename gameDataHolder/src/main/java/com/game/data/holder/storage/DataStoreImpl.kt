package com.game.data.holder.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class DataStoreImplementation(private val context: Context) : DataStoreRepository {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_data_holder_storage")

    override suspend fun getString(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.map { it[prefKey] }.first()
    }

    override fun putString(key: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val prefKey = stringPreferencesKey(key)
            context.dataStore.edit {
                it[prefKey] = value
            }
        }
    }
}