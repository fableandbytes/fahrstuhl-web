package com.fableandbytes.fahrstuhl.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    var totalScore: Int = 0,
    val achievements: List<Achievement> = emptyList()
)

@Serializable
data class Round(
    val floor: Int,
    val predictions: Map<Int, Int>, // PlayerID -> Prediction
    val results: Map<Int, Int>,    // PlayerID -> Actual Stiche
    val scores: Map<Int, Int>,      // PlayerID -> Round Score (this round only)
    val cumulativeScores: Map<Int, Int> // PlayerID -> Total Score after this round
)

@Serializable
data class GameHistoryEntry(
    val date: Long,
    val playerNames: List<String>,
    val rounds: List<Round>,
    val winnerName: String,
    val winnerScore: Int
)

enum class GamePhase {
    SETUP,
    START_PLAYER_SELECTION,
    PREDICTION,
    RESULT,
    SCOREBOARD,
    FINISHED,
    SETTINGS,
    REORDER_PLAYERS
}
