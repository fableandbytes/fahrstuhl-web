package com.fableandbytes.fahrstuhl.data

import com.fableandbytes.fahrstuhl.model.GameHistoryEntry

actual class FirebaseManager actual constructor() {
    actual suspend fun signInAnonymously(): String? {
        return null
    }

    actual suspend fun uploadHistory(history: List<GameHistoryEntry>) {
        // Firebase ist unter WasmJS aktuell nicht unterstützt
    }

    actual suspend fun fetchHistory(): Pair<List<GameHistoryEntry>, Long>? {
        return null
    }
}
