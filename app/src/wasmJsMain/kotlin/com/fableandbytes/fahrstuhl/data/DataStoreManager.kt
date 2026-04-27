package com.fableandbytes.fahrstuhl.data

import com.fableandbytes.fahrstuhl.model.GameHistoryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.browser.localStorage

actual class DataStoreManager actual constructor() {
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _history = MutableStateFlow<List<GameHistoryEntry>>(loadHistory())
    actual val gameHistory: Flow<List<GameHistoryEntry>> = _history.asStateFlow()

    private val _basePoints = MutableStateFlow(loadInt("basePoints", 10))
    actual val basePoints: Flow<Int> = _basePoints.asStateFlow()

    private val _pointsPerStich = MutableStateFlow(loadInt("pointsPerStich", 2))
    actual val pointsPerStich: Flow<Int> = _pointsPerStich.asStateFlow()

    private val _minusPoints = MutableStateFlow(loadInt("minusPoints", 10))
    actual val minusPoints: Flow<Int> = _minusPoints.asStateFlow()

    actual suspend fun saveScoringSettings(base: Int, perStich: Int, minus: Int) {
        saveInt("basePoints", base)
        saveInt("pointsPerStich", perStich)
        saveInt("minusPoints", minus)
        _basePoints.value = base
        _pointsPerStich.value = perStich
        _minusPoints.value = minus
    }

    actual suspend fun saveGameToHistory(entry: GameHistoryEntry) {
        val current = _history.value.toMutableList()
        current.add(0, entry)
        _history.value = current
        persistHistory(current)
    }

    actual suspend fun deleteGameFromHistory(timestamp: Long) {
        val updated = _history.value.filter { it.date != timestamp }
        _history.value = updated
        persistHistory(updated)
    }

    actual suspend fun getHistoryJson(): String {
        return json.encodeToString(_history.value)
    }

    actual suspend fun importHistory(jsonStr: String): Boolean {
        return try {
            val imported = json.decodeFromString<List<GameHistoryEntry>>(jsonStr)
            val current = _history.value.toMutableList()
            // Einfaches Merge: Nur neue Zeitstempel hinzufügen
            val existingTimestamps = current.map { it.date }.toSet()
            val newEntries = imported.filter { it.date !in existingTimestamps }
            
            if (newEntries.isNotEmpty()) {
                val merged = (newEntries + current).sortedByDescending { it.date }
                _history.value = merged
                persistHistory(merged)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    actual suspend fun mergeAndSaveHistory(newHistory: List<GameHistoryEntry>, cloudTimestamp: Long, force: Boolean) {
        // Für Web implementieren wir ein einfaches Merge
        importHistory(json.encodeToString(newHistory))
    }

    // Hilfsfunktionen für LocalStorage
    private fun loadHistory(): List<GameHistoryEntry> {
        val data = localStorage.getItem("game_history") ?: return emptyList()
        return try {
            json.decodeFromString(data)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun persistHistory(history: List<GameHistoryEntry>) {
        localStorage.setItem("game_history", json.encodeToString(history))
    }

    private fun loadInt(key: String, default: Int): Int {
        return localStorage.getItem(key)?.toIntOrNull() ?: default
    }

    private fun saveInt(key: String, value: Int) {
        localStorage.setItem(key, value.toString())
    }
}
