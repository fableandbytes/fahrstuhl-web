package com.fableandbytes.fahrstuhl.data

import com.fableandbytes.fahrstuhl.model.GameHistoryEntry
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class SharedHistoryData(
    val history: List<GameHistoryEntry>,
    val lastUpdated: Long
)

actual class FirebaseManager actual constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private fun getCurrentTimestampInternal(): Long = Clock.System.now().toEpochMilliseconds()

    actual suspend fun signInAnonymously(): String? {
        val currentUser = auth.currentUser
        if (currentUser != null) return currentUser.uid
        
        return try {
            val result = auth.signInAnonymously()
            result.user?.uid
        } catch (e: Exception) {
            println("FirebaseManager Error signing in: ${e.message}")
            null
        }
    }

    actual suspend fun uploadHistory(history: List<GameHistoryEntry>) {
        try {
            val data = SharedHistoryData(
                history = history,
                lastUpdated = getCurrentTimestampInternal()
            )
            db.collection("shared").document("game_history").set(data)
        } catch (e: Exception) {
            println("FirebaseManager Error uploading history: ${e.message}")
        }
    }

    actual suspend fun fetchHistory(): Pair<List<GameHistoryEntry>, Long>? {
        return try {
            val document = db.collection("shared").document("game_history").get()
            if (!document.exists) return emptyList<GameHistoryEntry>() to 0L
            
            val data = document.data<SharedHistoryData>()
            data.history to data.lastUpdated
        } catch (e: Exception) {
            println("FirebaseManager Error fetching history: ${e.message}")
            null
        }
    }
}

actual fun getCurrentTimestamp(): Long = Clock.System.now().toEpochMilliseconds()
