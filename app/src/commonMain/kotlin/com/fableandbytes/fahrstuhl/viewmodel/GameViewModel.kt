package com.fableandbytes.fahrstuhl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fableandbytes.fahrstuhl.data.DataStoreManager
import com.fableandbytes.fahrstuhl.data.FirebaseManager
import com.fableandbytes.fahrstuhl.model.*
import com.fableandbytes.fahrstuhl.logic.AchievementEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GameUiState(
    val players: List<Player> = emptyList(),
    val maxFloor: Int = 10,
    val currentFloorIndex: Int = 0,
    val floors: List<Int> = emptyList(),
    val currentPhase: GamePhase = GamePhase.SETUP,
    val rounds: List<Round> = emptyList(),
    val currentPredictions: Map<Int, Int?> = emptyMap(),
    val currentResults: Map<Int, Int?> = emptyMap(),
    val errorMessage: String? = null,
    val previousPhase: GamePhase? = null,
    val viewingFloorIndex: Int? = null,
    val basePointsCorrect: Int = 10,
    val pointsPerStichCorrect: Int = 2,
    val minusPointsPerStichDiff: Int = 10,
    val gameHistory: List<GameHistoryEntry> = emptyList(),
    val roundAchievements: Map<Int, List<Achievement>> = emptyMap(),
    val setupPlayerNames: List<String> = listOf("", ""),
    val setupMaxFloor: Int = 10
)

class GameViewModel : ViewModel() {
    private val dataStoreManager = DataStoreManager()
    private val firebaseManager = FirebaseManager()
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            firebaseManager.signInAnonymously()
            val cloudData = firebaseManager.fetchHistory()
            if (cloudData != null) {
                val (cloudHistory, cloudTimestamp) = cloudData as Pair<List<GameHistoryEntry>, Long>
                dataStoreManager.mergeAndSaveHistory(cloudHistory, cloudTimestamp)
            }
        }

        // Beobachte Änderungen und aktualisiere den UI-Status
        viewModelScope.launch {
            combine(
                dataStoreManager.basePoints,
                dataStoreManager.pointsPerStich,
                dataStoreManager.minusPoints,
                dataStoreManager.gameHistory
            ) { base, perStich, minus, history ->
                _uiState.update { 
                    it.copy(
                        basePointsCorrect = base,
                        pointsPerStichCorrect = perStich,
                        minusPointsPerStichDiff = minus,
                        gameHistory = history
                    )
                }
            }.collect()
        }

        // Separater Collector für den Cloud-Upload
        viewModelScope.launch {
            dataStoreManager.gameHistory
                .distinctUntilChanged()
                .collect { history ->
                    firebaseManager.uploadHistory(history)
                }
        }
    }

    fun startGame(playerNames: List<String>, maxFloor: Int) {
        val players = playerNames.mapIndexed { index, name -> Player(id = index, name = name) }
        val floors = (1..maxFloor).toList() + (maxFloor - 1 downTo 1).toList()
        
        _uiState.update {
            it.copy(
                players = players,
                maxFloor = maxFloor,
                floors = floors,
                currentFloorIndex = 0,
                currentPhase = GamePhase.START_PLAYER_SELECTION,
                currentPredictions = players.associate { p -> p.id to null },
                currentResults = players.associate { p -> p.id to null },
                rounds = emptyList(),
                setupPlayerNames = playerNames,
                setupMaxFloor = maxFloor
            )
        }
    }

    fun updatePlayerOrder(orderedPlayers: List<Player>) {
        _uiState.update { it.copy(players = orderedPlayers) }
    }

    fun startGameAfterSelection() {
        _uiState.update { it.copy(currentPhase = GamePhase.PREDICTION, previousPhase = null) }
    }

    fun goBackToSetup() {
        _uiState.update { it.copy(currentPhase = GamePhase.SETUP) }
    }

    fun setPrediction(playerId: Int, prediction: Int) {
        _uiState.update { state ->
            val updatedPredictions = state.currentPredictions.toMutableMap()
            updatedPredictions[playerId] = prediction
            state.copy(currentPredictions = updatedPredictions, errorMessage = null)
        }
    }

    fun setResult(playerId: Int, result: Int) {
        _uiState.update { state ->
            val updatedResults = state.currentResults.toMutableMap()
            updatedResults[playerId] = result
            state.copy(currentResults = updatedResults, errorMessage = null)
        }
    }

    fun submitPredictions() {
        val state = _uiState.value
        val floor = state.floors[state.currentFloorIndex]
        
        if (state.currentPredictions.values.any { it == null }) {
            _uiState.update { it.copy(errorMessage = "Alle Spieler müssen eine Ansage machen.") }
            return
        }

        val totalPredictions = state.currentPredictions.values.filterNotNull().sum()
        if (totalPredictions == floor) {
            _uiState.update { it.copy(errorMessage = "Die Summe der Ansagen darf nicht $floor sein!") }
            return
        }

        _uiState.update { it.copy(currentPhase = GamePhase.RESULT, errorMessage = null) }
    }

    fun submitResults() {
        val state = _uiState.value
        val floor = state.floors[state.currentFloorIndex]

        if (state.currentResults.values.any { it == null }) {
            _uiState.update { it.copy(errorMessage = "Alle Spieler müssen ihre Stiche eingeben.") }
            return
        }

        val totalResults = state.currentResults.values.filterNotNull().sum()
        if (totalResults != floor) {
            _uiState.update { it.copy(errorMessage = "Die Summe der Stiche muss exakt $floor sein (aktuell: $totalResults).") }
            return
        }

        calculateRoundScores()
    }

    private fun calculateRoundScores() {
        val state = _uiState.value
        val roundScores = mutableMapOf<Int, Int>()
        val cumulativeScores = mutableMapOf<Int, Int>()
        
        val updatedPlayersWithPoints = state.players.map { player ->
            val x = state.currentPredictions[player.id]!!
            val actual = state.currentResults[player.id]!!
            
            val base = state.basePointsCorrect
            val multiplier = state.pointsPerStichCorrect
            
            val score = if (x == actual) {
                base + (multiplier * x)
            } else {
                val diff = kotlin.math.abs(x - actual)
                base - (state.minusPointsPerStichDiff * diff)
            }
            
            roundScores[player.id] = score
            val newTotal = player.totalScore + score
            cumulativeScores[player.id] = newTotal
            player.copy(totalScore = newTotal)
        }

        val newRound = Round(
            floor = state.floors[state.currentFloorIndex],
            predictions = state.currentPredictions.mapValues { it.value!! },
            results = state.currentResults.mapValues { it.value!! },
            scores = roundScores,
            cumulativeScores = cumulativeScores
        )

        val rounds = state.rounds + newRound
        // Recalculate Achievements
        val playersWithAchievements = if (state.currentFloorIndex == state.floors.size - 1) {
            AchievementEngine.calculateEndGameAchievements(updatedPlayersWithPoints, rounds)
        } else {
            updatedPlayersWithPoints
        }
        
        val intermediate = AchievementEngine.getIntermediateAchievements(playersWithAchievements, rounds)

        _uiState.update { 
            it.copy(
                players = playersWithAchievements,
                rounds = rounds,
                currentPhase = GamePhase.SCOREBOARD,
                errorMessage = null,
                roundAchievements = intermediate
            )
        }
    }

    fun nextRound() {
        val state = _uiState.value
        val nextFloorIndex = state.currentFloorIndex + 1
        val isFinished = nextFloorIndex >= state.floors.size

        if (isFinished) {
            saveGameToHistory()
            _uiState.update { it.copy(currentPhase = GamePhase.FINISHED) }
        } else {
            _uiState.update {
                it.copy(
                    currentFloorIndex = nextFloorIndex,
                    currentPhase = GamePhase.PREDICTION,
                    currentPredictions = it.players.associate { p -> p.id to null },
                    currentResults = it.players.associate { p -> p.id to null },
                    errorMessage = null
                )
            }
        }
    }

    private fun saveGameToHistory() {
        val state = _uiState.value
        val winner = state.players.maxByOrNull { it.totalScore } ?: return
        val entry = GameHistoryEntry(
            date = com.fableandbytes.fahrstuhl.data.getCurrentTimestamp(),
            playerNames = state.players.map { it.name },
            rounds = state.rounds,
            winnerName = winner.name,
            winnerScore = winner.totalScore
        )
        viewModelScope.launch {
            dataStoreManager.saveGameToHistory(entry)
        }
    }

    fun deleteGameFromHistory(timestamp: Long) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(gameHistory = state.gameHistory.filter { it.date != timestamp })
            }
            dataStoreManager.deleteGameFromHistory(timestamp)
        }
    }

    fun showScoreboard() {
        _uiState.update { it.copy(previousPhase = it.currentPhase, currentPhase = GamePhase.SCOREBOARD) }
    }

    fun showSettings() {
        val current = _uiState.value.currentPhase
        if (current == GamePhase.SETTINGS) return
        
        _uiState.update { 
            it.copy(
                previousPhase = if (current == GamePhase.REORDER_PLAYERS) it.previousPhase else current,
                currentPhase = GamePhase.SETTINGS
            ) 
        }
    }

    fun startReordering() {
        val current = _uiState.value.currentPhase
        if (current == GamePhase.REORDER_PLAYERS) return

        _uiState.update { 
            it.copy(
                previousPhase = if (current == GamePhase.SETTINGS) it.previousPhase else current,
                currentPhase = GamePhase.REORDER_PLAYERS
            ) 
        }
    }

    fun goBack() {
        _uiState.update { state ->
            when (state.currentPhase) {
                GamePhase.REORDER_PLAYERS -> state.copy(currentPhase = GamePhase.SETTINGS)
                GamePhase.RESULT -> state.copy(currentPhase = GamePhase.PREDICTION)
                else -> {
                    val nextPhase = state.previousPhase ?: GamePhase.SETUP
                    state.copy(currentPhase = nextPhase, previousPhase = null, viewingFloorIndex = null)
                }
            }
        }
    }

    fun viewPreviousFloor() {
        _uiState.update { state ->
            val currentIndex = state.viewingFloorIndex ?: state.currentFloorIndex
            if (currentIndex > 0) {
                state.copy(viewingFloorIndex = currentIndex - 1)
            } else state
        }
    }

    fun viewNextFloor() {
        _uiState.update { state ->
            val currentIndex = state.viewingFloorIndex ?: return@update state
            if (currentIndex < state.currentFloorIndex) {
                val nextIndex = currentIndex + 1
                state.copy(viewingFloorIndex = if (nextIndex == state.currentFloorIndex) null else nextIndex)
            } else state
        }
    }

    fun exitHistoryView() {
        _uiState.update { it.copy(viewingFloorIndex = null) }
    }

    fun updateScoringSettings(base: Int, perStich: Int, minusPerDiff: Int) {
        viewModelScope.launch {
            dataStoreManager.saveScoringSettings(base, perStich, minusPerDiff)
        }
    }

    fun resetToSetup() {
        _uiState.update { 
            GameUiState(
                currentPhase = GamePhase.SETUP,
                basePointsCorrect = it.basePointsCorrect,
                pointsPerStichCorrect = it.pointsPerStichCorrect,
                minusPointsPerStichDiff = it.minusPointsPerStichDiff,
                gameHistory = it.gameHistory
            )
        }
    }
    
    fun getStartPlayerIndex(): Int {
        val state = _uiState.value
        if (state.players.isEmpty()) return 0
        return state.currentFloorIndex % state.players.size
    }

    fun exportHistory(onExport: (String) -> Unit) {
        viewModelScope.launch {
            onExport(dataStoreManager.getHistoryJson())
        }
    }

    fun importHistory(json: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = dataStoreManager.importHistory(json)
            onComplete(success)
        }
    }

    fun isLastPlayer(playerId: Int): Boolean {
        val state = _uiState.value
        if (state.players.isEmpty()) return false
        val startPlayerIndex = getStartPlayerIndex()
        val lastPlayerIndex = (startPlayerIndex + state.players.size - 1 + state.players.size) % state.players.size
        return state.players.indexOfFirst { it.id == playerId } == lastPlayerIndex
    }

    fun getForbiddenPredictionForLastPlayer(): Int? {
        val state = _uiState.value
        val floor = state.floors.getOrNull(state.currentFloorIndex) ?: return null
        
        val startPlayerIndex = getStartPlayerIndex()
        val lastPlayerIndex = (startPlayerIndex + state.players.size - 1 + state.players.size) % state.players.size
        val lastPlayerId = state.players[lastPlayerIndex].id
        
        val otherPredictions = state.currentPredictions.filterKeys { it != lastPlayerId }.values
        
        if (otherPredictions.any { it == null }) return null
        
        val sumOthers = otherPredictions.filterNotNull().sum()
        val forbidden = floor - sumOthers
        return if (forbidden in 0..floor) forbidden else null
    }
}
