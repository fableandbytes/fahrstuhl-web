package com.fableandbytes.fahrstuhl.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fableandbytes.fahrstuhl.model.GamePhase
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.ui.components.*
import com.fableandbytes.fahrstuhl.ui.theme.*

@Composable
fun FahrstuhlApp(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onExportHistory: (String) -> Unit = {},
    onImportHistory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier) {
        when (uiState.currentPhase) {
            GamePhase.SETUP -> SetupScreen(viewModel, onExportHistory, onImportHistory)
            GamePhase.START_PLAYER_SELECTION -> StartPlayerScreen(viewModel)
            GamePhase.PREDICTION, GamePhase.RESULT -> GameScreen(viewModel)
            GamePhase.SCOREBOARD -> ScoreboardScreen(viewModel)
            GamePhase.SETTINGS -> SettingsScreen(viewModel)
            GamePhase.REORDER_PLAYERS -> ReorderPlayersScreen(viewModel)
            GamePhase.FINISHED -> FinishedScreen(viewModel)
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isViewingHistory = uiState.viewingFloorIndex != null
    val currentFloorIndex = uiState.viewingFloorIndex ?: uiState.currentFloorIndex
    val floor = uiState.floors.getOrNull(currentFloorIndex) ?: 0
    val startPlayerIndex = if (uiState.players.isEmpty()) 0 else currentFloorIndex % uiState.players.size
    
    // Rotate players so the start player for the current floor is at the top
    val rotatedPlayers = if (uiState.players.isNotEmpty()) {
        uiState.players.drop(startPlayerIndex) + uiState.players.take(startPlayerIndex)
    } else {
        emptyList()
    }

    val sortedPlayers = uiState.players.sortedByDescending { it.totalScore }
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (isViewingHistory) {
            viewModel.exitHistoryView()
        } else if (uiState.currentPhase == GamePhase.RESULT) {
            viewModel.goBack()
        } else {
            showExitDialog = true
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Spiel beenden?", color = ElevatorGold, fontWeight = FontWeight.Bold) },
            text = { Text("Möchtest du das aktuelle Spiel wirklich beenden und zum Hauptmenü zurückkehren? Der Fortschritt geht verloren.", color = ElevatorDarkBlue) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.resetToSetup()
                }) {
                    Text("Beenden", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Abbrechen", color = ElevatorDarkBlue)
                }
            },
            containerColor = ElevatorCream
        )
    }

    DecorativeBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Top Icons - Positioned in corners
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { showExitDialog = true }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Spiel abbrechen", 
                        tint = ElevatorGold, 
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Row {
                    IconButton(onClick = { viewModel.showSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Einstellungen", tint = ElevatorGold, modifier = Modifier.size(28.dp))
                    }

                    IconButton(onClick = { viewModel.showScoreboard() }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Scoreboard", tint = ElevatorGold, modifier = Modifier.size(28.dp))
                    }
                }
            }

            // Historie Tag - moved up into the notch area under the logo
            if (isViewingHistory) {
                Text(
                    "HISTORIE",
                    color = ElevatorGold.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 138.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fixed top section to prevent layout shifting
                Column(
                    modifier = Modifier.height(370.dp), // Set to 370dp to ensure both arrows have full size (162 + 208 for indicator)
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Space for the Logo and Notch
                    // Moved down slightly (from 150dp to 162dp)
                    Spacer(modifier = Modifier.height(162.dp))

                    // Visual Elevator Indicator
                    ElevatorIndicator(
                        floor = floor,
                        onUpClick = { viewModel.viewNextFloor() },
                        onDownClick = { viewModel.viewPreviousFloor() },
                        canGoUp = isViewingHistory || currentFloorIndex < uiState.currentFloorIndex,
                        canGoDown = currentFloorIndex > 0
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .padding(bottom = 8.dp) // Removed top padding
                ) {
                itemsIndexed(rotatedPlayers, key = { _, player -> player.id }) { index, player ->
                    val isStartPlayer = index == 0
                    
                    val (prediction, result) = if (isViewingHistory) {
                        val round = uiState.rounds.getOrNull(currentFloorIndex)
                        round?.predictions?.get(player.id) to round?.results?.get(player.id)
                    } else {
                        uiState.currentPredictions[player.id] to uiState.currentResults[player.id]
                    }

                    val rank = sortedPlayers.count { it.totalScore > player.totalScore } + 1
                    val forbiddenPrediction = if (index == rotatedPlayers.size - 1 && uiState.currentPhase == GamePhase.PREDICTION && !isViewingHistory) {
                        viewModel.getForbiddenPredictionForLastPlayer()
                    } else null

                    @OptIn(ExperimentalFoundationApi::class)
                    PlayerGameRow(
                        player = player,
                        rank = rank,
                        isStartPlayer = isStartPlayer,
                        prediction = prediction,
                        result = result,
                        onPredictionChange = { viewModel.setPrediction(player.id, it) },
                        onResultChange = { viewModel.setResult(player.id, it) },
                        maxVal = floor,
                        forbiddenValue = forbiddenPrediction,
                        phase = if (isViewingHistory) GamePhase.FINISHED else uiState.currentPhase,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isViewingHistory) {
                Button(
                    onClick = {
                        if (uiState.currentPhase == GamePhase.PREDICTION) {
                            viewModel.submitPredictions()
                        } else {
                            viewModel.submitResults()
                        }
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.errorMessage != null) Color.Gray else ArtDecoGreen
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        if (uiState.currentPhase == GamePhase.PREDICTION) "ANSAGEN BESTÄTIGEN" else "RUNDE AUSWERTEN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.exitHistoryView() },
                    modifier = Modifier
                        .width(220.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElevatorGold),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("ZURÜCK ZUM SPIEL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ElevatorDarkBlue)
                }
            }
            
            if (!isViewingHistory) {
                uiState.errorMessage?.let {
                    Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }
            }
            } // End of Column
        } // End of Box
    }
}
