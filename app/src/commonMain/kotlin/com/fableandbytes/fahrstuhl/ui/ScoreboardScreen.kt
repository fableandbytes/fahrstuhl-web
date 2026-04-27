package com.fableandbytes.fahrstuhl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.ui.theme.*
import com.fableandbytes.fahrstuhl.ui.components.*
import com.fableandbytes.fahrstuhl.model.*

@Composable
fun ScoreboardScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    // For intermediate achievements popup
    var showIntermediateAchievement by remember { mutableStateOf<Pair<Int, Achievement>?>(null) }
    
    LaunchedEffect(uiState.roundAchievements) {
        if (uiState.roundAchievements.isNotEmpty()) {
            val entry = uiState.roundAchievements.entries.random()
            if (entry.value.isNotEmpty()) {
                showIntermediateAchievement = entry.key to entry.value.random()
            }
        }
    }

    // Only allow back navigation if we are "viewing" the scoreboard, not during round transition
    if (uiState.previousPhase != null) {
        BackHandler {
            viewModel.goBack()
        }
    }
    
    val groupedPlayers: Map<Int, List<Player>> = uiState.players.groupBy { it.totalScore }
    // If no points have been scored yet (total scores all 0), don't show anyone on the podium
    val hasScores: Boolean = uiState.players.any { it.totalScore > 0 }
    val sortedScores: List<Int> = if (hasScores) {
        groupedPlayers.keys.toList().sortedDescending()
    } else {
        emptyList<Int>()
    }
    
    var showExplanation by remember { mutableStateOf(false) }
    var showScoringExplanation by remember { mutableStateOf(false) }

    if (showIntermediateAchievement != null) {
        val (playerId, achievement) = showIntermediateAchievement!!
        val player = uiState.players.find { it.id == playerId }
        AlertDialog(
            onDismissRequest = { showIntermediateAchievement = null },
            icon = { Icon(achievement.icon, contentDescription = null, tint = ElevatorGold, modifier = Modifier.size(48.dp)) },
            title = { Text(achievement.title, color = ElevatorDarkBlue, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(player?.name ?: "Spieler", style = MaterialTheme.typography.titleMedium, color = ElevatorGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(achievement.description, textAlign = TextAlign.Center, color = ElevatorDarkBlue, style = MaterialTheme.typography.bodySmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(achievement.flavorText, textAlign = TextAlign.Center, color = ElevatorDarkBlue)
                }
            },
            confirmButton = {
                TextButton(onClick = { showIntermediateAchievement = null }) {
                    Text("Super!", color = ArtDecoGreen, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = ElevatorCream
        )
    }

    if (showExplanation) {
        AlertDialog(
            onDismissRequest = { showExplanation = false },
            title = { Text("Legende: Etagen", color = ElevatorGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Symbole bei Etage:", color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowDropUp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        Text(" Überboten (Ansagen > Etage)", fontSize = 14.sp, color = ElevatorDarkBlue)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ArtDecoGreen, modifier = Modifier.size(20.dp))
                        Text(" Unterboten (Ansagen < Etage)", fontSize = 14.sp, color = ElevatorDarkBlue)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Diese Symbole zeigen an, ob in der Runde insgesamt mehr oder weniger Stiche angesagt wurden, als Karten im Spiel sind.", fontSize = 12.sp, color = ElevatorDarkBlue.copy(alpha = 0.7f))
                }
            },
            confirmButton = {
                TextButton(onClick = { showExplanation = false }) {
                    Text("Verstanden", color = ElevatorDarkBlue)
                }
            },
            containerColor = ElevatorCream
        )
    }

    if (showScoringExplanation) {
        AlertDialog(
            onDismissRequest = { showScoringExplanation = false },
            title = { Text("Legende: Punkte & Details", color = ElevatorGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Zellen-Aufbau:", color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1. Ansage → Stiche (z.B. 2 → 2)", fontSize = 13.sp, color = ElevatorDarkBlue)
                    Text("2. Aktueller Gesamtpunktestand", fontSize = 13.sp, color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
                    Text("3. (Punkte in dieser Runde)", fontSize = 13.sp, color = ElevatorDarkBlue.copy(alpha = 0.7f))
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Berechnung pro Runde:", color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text("Basispunkte: ${uiState.basePointsCorrect} pro Spiel", fontSize = 13.sp, color = ElevatorDarkBlue)
                    Text("Treffer (Ansage x): ${uiState.basePointsCorrect} + ${uiState.pointsPerStichCorrect}*x", fontSize = 13.sp, color = ArtDecoGreen, fontWeight = FontWeight.Bold)
                    Text("Zu wenig Stiche: ${uiState.basePointsCorrect} - ${uiState.pointsPerStichCorrect}*x", fontSize = 13.sp, color = Color.Red)
                    Text("Zu viele Stiche: ${uiState.basePointsCorrect} - ${uiState.pointsPerStichCorrect}*(gemachte Stiche)", fontSize = 13.sp, color = Color.Red)
                }
            },
            confirmButton = {
                TextButton(onClick = { showScoringExplanation = false }) {
                    Text("Verstanden", color = ElevatorDarkBlue)
                }
            },
            containerColor = ElevatorCream
        )
    }

    DecorativeBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title integrated into the decorative background area (starts at 80dp)
            Spacer(modifier = Modifier.height(180.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.previousPhase != null) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = ElevatorDarkBlue)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
                Text(
                    "Punkteübersicht",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElevatorDarkBlue,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp)) 
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Leaderboard Podium
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd
                val secondGroup = sortedScores.getOrNull(1)?.let { groupedPlayers[it] } ?: emptyList()
                if (secondGroup.isNotEmpty()) PodiumPlace(secondGroup, 65.dp, Color(0xFFC0C0C0))
                
                // 1st
                val firstGroup = sortedScores.getOrNull(0)?.let { groupedPlayers[it] } ?: emptyList()
                if (firstGroup.isNotEmpty()) PodiumPlace(firstGroup, 90.dp, ElevatorGold)
                
                // 3rd
                val thirdGroup = sortedScores.getOrNull(2)?.let { groupedPlayers[it] } ?: emptyList()
                if (thirdGroup.isNotEmpty()) PodiumPlace(thirdGroup, 50.dp, Color(0xFFCD7F32))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Leaderboard Badge (Full Width)
            Surface(
                color = ElevatorDarkBlue,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Leaderboard", 
                    color = ElevatorGold, 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            // Table Container
            ScoreboardTable(
                players = uiState.players,
                rounds = uiState.rounds,
                basePoints = uiState.basePointsCorrect,
                onFloorClick = { showExplanation = true },
                onCellClick = { showScoringExplanation = true },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.previousPhase == null) {
                Button(
                    onClick = { viewModel.nextRound() },
                    modifier = Modifier
                        .width(220.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArtDecoGreen),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("NÄCHSTE ETAGE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
