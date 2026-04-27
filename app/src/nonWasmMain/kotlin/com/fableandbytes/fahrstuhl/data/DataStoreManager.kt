package com.fableandbytes.fahrstuhl.data

import androidx.datastore.preferences.core.*
import com.fableandbytes.fahrstuhl.model.GameHistoryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

actual class DataStoreManager actual constructor() {
    private val dataStore = createDataStore()
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        val BASE_POINTS = intPreferencesKey("base_points")
        val POINTS_PER_STICH = intPreferencesKey("points_per_stich")
        val MINUS_POINTS = intPreferencesKey("minus_points")
        val GAME_HISTORY = stringPreferencesKey("game_history")
        val LAST_UPDATED = longPreferencesKey("last_updated")
    }

    actual suspend fun saveScoringSettings(base: Int, perStich: Int, minus: Int) {
        dataStore.edit { preferences ->
            preferences[BASE_POINTS] = base
            preferences[POINTS_PER_STICH] = perStich
            preferences[MINUS_POINTS] = minus
        }
    }

    actual val basePoints: Flow<Int> = dataStore.data.map { it[BASE_POINTS] ?: 10 }
    actual val pointsPerStich: Flow<Int> = dataStore.data.map { it[POINTS_PER_STICH] ?: 2 }
    actual val minusPoints: Flow<Int> = dataStore.data.map { it[MINUS_POINTS] ?: 10 }
    val lastUpdated: Flow<Long> = dataStore.data.map { it[LAST_UPDATED] ?: 0L }

    actual suspend fun saveGameToHistory(entry: GameHistoryEntry) {
        dataStore.edit { preferences ->
            val currentHistoryJson = preferences[GAME_HISTORY] ?: "[]"
            val history: MutableList<GameHistoryEntry> = json.decodeFromString(currentHistoryJson)
            history.add(0, entry)
            preferences[GAME_HISTORY] = json.encodeToString(history)
            preferences[LAST_UPDATED] = Clock.System.now().toEpochMilliseconds()
        }
    }

    actual suspend fun deleteGameFromHistory(timestamp: Long) {
        dataStore.edit { preferences ->
            val currentHistoryJson = preferences[GAME_HISTORY] ?: "[]"
            val history: MutableList<GameHistoryEntry> = json.decodeFromString(currentHistoryJson)
            
            val initialSize = history.size
            history.removeAll { it.date == timestamp }
            
            if (history.size != initialSize) {
                preferences[GAME_HISTORY] = json.encodeToString(history)
            }
        }
    }

    actual val gameHistory: Flow<List<GameHistoryEntry>> = dataStore.data.map { preferences ->
        val jsonStr = preferences[GAME_HISTORY] ?: "[]"
        try {
            json.decodeFromString<List<GameHistoryEntry>>(jsonStr)
        } catch (e: Exception) {
            emptyList()
        }
    }

    actual suspend fun getHistoryJson(): String {
        return dataStore.data.map { it[GAME_HISTORY] ?: "[]" }.first()
    }

    actual suspend fun importHistory(jsonStr: String): Boolean {
        return try {
            val importedHistory: List<GameHistoryEntry> = json.decodeFromString(jsonStr)
            mergeAndSaveHistory(importedHistory, force = true)
            true
        } catch (e: Exception) {
            false
        }
    }

    actual suspend fun mergeAndSaveHistory(newHistory: List<GameHistoryEntry>, cloudTimestamp: Long, force: Boolean) {
        dataStore.edit { preferences ->
            val currentTimestamp = preferences[LAST_UPDATED] ?: 0L
            
            if (force || cloudTimestamp > currentTimestamp) {
                val currentHistoryJson = preferences[GAME_HISTORY] ?: "[]"
                val currentHistory: List<GameHistoryEntry> = json.decodeFromString(currentHistoryJson)

                val mergedHistory = (currentHistory + newHistory)
                    .distinctBy { it.date }
                    .sortedByDescending { it.date }

                preferences[GAME_HISTORY] = json.encodeToString(mergedHistory)
                if (cloudTimestamp > currentTimestamp) {
                    preferences[LAST_UPDATED] = cloudTimestamp
                }
            }
        }
    }
}
