package com.fableandbytes.fahrstuhl.data

import com.fableandbytes.fahrstuhl.model.GameHistoryEntry
import kotlinx.coroutines.flow.Flow

expect class FirebaseManager() {
    suspend fun signInAnonymously(): String?
    suspend fun uploadHistory(history: List<GameHistoryEntry>)
    suspend fun fetchHistory(): Pair<List<GameHistoryEntry>, Long>?
}

expect class DataStoreManager() {
    val basePoints: Flow<Int>
    val pointsPerStich: Flow<Int>
    val minusPoints: Flow<Int>
    val gameHistory: Flow<List<GameHistoryEntry>>

    suspend fun saveScoringSettings(base: Int, perStich: Int, minus: Int)
    suspend fun saveGameToHistory(entry: GameHistoryEntry)
    suspend fun deleteGameFromHistory(timestamp: Long)
    suspend fun getHistoryJson(): String
    suspend fun importHistory(jsonStr: String): Boolean
    suspend fun mergeAndSaveHistory(newHistory: List<GameHistoryEntry>, cloudTimestamp: Long = 0L, force: Boolean = false)
}

expect fun getCurrentTimestamp(): Long
