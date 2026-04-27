package com.fableandbytes.fahrstuhl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fableandbytes.fahrstuhl.ui.theme.*
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.ui.components.*
import com.fableandbytes.fahrstuhl.model.Achievement
import com.fableandbytes.fahrstuhl.model.Player

@Composable
fun FinishedScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val sortedPlayers = uiState.players.sortedByDescending { it.totalScore }
    val winner = sortedPlayers.firstOrNull()
    var showExitDialog by remember { mutableStateOf(false) }
    var selectedPlayerForDetails by remember { mutableStateOf<Player?>(null) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Neues Spiel?", color = ElevatorGold, fontWeight = FontWeight.Bold) },
            text = { Text("Möchtest du zum Hauptmenü zurückkehren, um ein neues Spiel zu starten?", color = ElevatorDarkBlue) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.resetToSetup()
                }) {
                    Text("Neues Spiel", color = ArtDecoGreen, fontWeight = FontWeight.Bold)
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
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(170.dp))
            
            Text("★ ABSCHLUSS-ZEREMONIE ★", style = MaterialTheme.typography.headlineSmall, color = ElevatorGold, fontWeight = FontWeight.Black)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Der goldene Liftboy:", color = ElevatorDarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            winner?.name ?: "", 
                            color = ElevatorGold, 
                            fontSize = 42.sp, 
                            fontWeight = FontWeight.Black,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { selectedPlayerForDetails = winner }
                        )
                        Text("${winner?.totalScore ?: 0} Punkte", color = ElevatorDarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                items(sortedPlayers) { player ->
                    AchievementCard(
                        player = player,
                        onClick = { selectedPlayerForDetails = player }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (selectedPlayerForDetails != null) {
                PlayerAchievementDetailDialog(
                    player = selectedPlayerForDetails!!,
                    onDismiss = { selectedPlayerForDetails = null }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.resetToSetup() },
                modifier = Modifier.width(220.dp).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArtDecoGreen),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("ZURÜCK ZUR LOBBY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun AchievementCard(player: Player, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ElevatorDarkBlue),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, ElevatorGold)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(player.name, color = ElevatorGold, fontWeight = FontWeight.Black, fontSize = 22.sp)
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = ElevatorGold,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${player.totalScore} Pkt", 
                        color = ElevatorDarkBlue, 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            
            if (player.achievements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                // Row of large achievement icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    player.achievements.forEach { achievement ->
                        Surface(
                            modifier = Modifier.size(56.dp).padding(4.dp),
                            shape = CircleShape,
                            color = ElevatorGold,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = achievement.icon, 
                                contentDescription = achievement.title, 
                                tint = ElevatorDarkBlue, 
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
                Text(
                    "Tippen für Details", 
                    color = ElevatorCream.copy(alpha = 0.6f), 
                    fontSize = 11.sp, 
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
            } else {
                Text(
                    "Ein unauffälliger Fahrgast.", 
                    color = ElevatorCream.copy(alpha = 0.5f), 
                    fontSize = 12.sp, 
                    modifier = Modifier.padding(top = 8.dp),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PlayerAchievementDetailDialog(player: Player, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Auszeichnungen", 
                    color = ElevatorDarkBlue.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    player.name, 
                    color = ElevatorGold, 
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center
                ) 
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (player.achievements.isEmpty()) {
                    Text(
                        "Dieser Spieler hat sich unauffällig im Hintergrund gehalten.",
                        color = ElevatorDarkBlue,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    player.achievements.forEach { achievement ->
                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElevatorDarkBlue.copy(alpha = 0.05f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = ElevatorGold
                            ) {
                                Icon(
                                    imageVector = achievement.icon, 
                                    contentDescription = null, 
                                    tint = ElevatorDarkBlue, 
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(achievement.title, color = ElevatorDarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    achievement.description, 
                                    color = ElevatorDarkBlue.copy(alpha = 0.8f), 
                                    fontSize = 12.sp, 
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    achievement.flavorText, 
                                    color = ElevatorDarkBlue, 
                                    fontSize = 14.sp, 
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("VERSTANDEN", color = ArtDecoGreen, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = ElevatorCream,
        shape = RoundedCornerShape(24.dp)
    )
}
