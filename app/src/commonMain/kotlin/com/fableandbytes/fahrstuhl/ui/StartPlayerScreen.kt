package com.fableandbytes.fahrstuhl.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
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
import com.fableandbytes.fahrstuhl.model.Player
import com.fableandbytes.fahrstuhl.ui.components.DecorativeBackground
import com.fableandbytes.fahrstuhl.ui.theme.*
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Composable
fun StartPlayerScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val players = uiState.players
    var currentPlayers by remember { mutableStateOf(players) }
    var isRolling by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler {
        viewModel.goBackToSetup()
    }

    DecorativeBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Back Button at same position as Settings in SetupScreen
            IconButton(
                onClick = { viewModel.goBackToSetup() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Zurück", 
                    tint = ElevatorGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(180.dp))

                Text(
                    "Wer beginnt?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElevatorDarkBlue,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Der oberste Spieler beginnt die erste Runde.",
                    fontSize = 14.sp,
                    color = ElevatorDarkBlue.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Player List with Animation (Scrollable)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, ElevatorDarkBlue.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        itemsIndexed(currentPlayers, key = { _, player -> player.id }) { index, player ->
                            @OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
                            PlayerSelectionRow(
                                player = player,
                                isFirst = index == 0,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isRolling) {
                                scope.launch {
                                    isRolling = true
                                    val startTime: Long = Clock.System.now().toEpochMilliseconds()
                                    val duration: Long = 3000L 
                                    
                                    while (Clock.System.now().toEpochMilliseconds() - startTime < duration) {
                                        val elapsed: Long = Clock.System.now().toEpochMilliseconds() - startTime
                                        val progress: Float = elapsed.toFloat() / duration.toFloat()
                                        val delayTime: Long = (60f + (progress * progress * progress * progress * 800f)).toLong()
                                        currentPlayers = currentPlayers.drop(1) + currentPlayers.take(1)
                                        delay(delayTime)
                                    }
                                    isRolling = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElevatorGold),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !isRolling,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = null, tint = ElevatorDarkBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("STARTSPIELER WÜRFELN", color = ElevatorDarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Button(
                        onClick = { 
                            viewModel.updatePlayerOrder(currentPlayers)
                            viewModel.startGameAfterSelection()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArtDecoGreen),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !isRolling,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("SPIEL STARTEN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerSelectionRow(player: Player, isFirst: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        color = if (isFirst) ElevatorDarkBlue else Color.White,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isFirst) 8.dp else 2.dp,
        shadowElevation = if (isFirst) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = player.name,
                color = if (isFirst) ElevatorGold else ElevatorDarkBlue,
                fontSize = 18.sp,
                fontWeight = if (isFirst) FontWeight.Black else FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            if (isFirst) {
                Text(
                    "BEGINNT",
                    color = ElevatorGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .border(1.dp, ElevatorGold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
