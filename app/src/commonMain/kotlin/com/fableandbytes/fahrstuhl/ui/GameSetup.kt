package com.fableandbytes.fahrstuhl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalFocusManager
import com.fableandbytes.fahrstuhl.ui.theme.*
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.ui.components.*

@Composable
fun SetupScreen(
    viewModel: GameViewModel,
    onExportHistory: (String) -> Unit = {},
    onImportHistory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var playerNames by remember { mutableStateOf(uiState.setupPlayerNames.ifEmpty { listOf("", "") }) }
    var maxFloor by remember { mutableFloatStateOf(uiState.setupMaxFloor.toFloat()) }
    var showError by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("App beenden?", color = ElevatorGold, fontWeight = FontWeight.Bold) },
            text = { Text("Möchtest du die App wirklich verlassen?", color = ElevatorDarkBlue) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    // Since it's the root screen, we can't "go back", so we stay or the system exits
                    // Platform specific exit would go here if needed, but for now we just close the dialog
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

    if (showHistory) {
        HistoryDialog(
            history = uiState.gameHistory, 
            onDismiss = { showHistory = false },
            onDeleteEntry = { viewModel.deleteGameFromHistory(it) },
            onExport = { 
                viewModel.exportHistory { json ->
                    onExportHistory(json)
                }
            },
            onImport = onImportHistory
        )
    }

    val focusManager = LocalFocusManager.current

    DecorativeBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Top Icons - Positioned in corners like in GameScreen
            IconButton(
                onClick = { viewModel.showSettings() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Einstellungen",
                    tint = ElevatorGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = { showHistory = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Logbuch",
                    tint = ElevatorGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title position matching Scoreboard and Settings
                Spacer(modifier = Modifier.height(180.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Setup",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ElevatorDarkBlue,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        "SPIELER",
                        color = ElevatorDarkBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(vertical = 4.dp)
                    ) {
                        itemsIndexed(playerNames) { index, name ->
                            TextField(
                                value = name,
                                onValueChange = { newName ->
                                    val newList = playerNames.toMutableList()
                                    newList[index] = newName
                                    playerNames = newList
                                },
                                placeholder = {
                                    Text(
                                        "Spieler ${index + 1}",
                                        color = ElevatorDarkBlue.copy(alpha = 0.4f),
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .height(52.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    color = ElevatorDarkBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = ElevatorDarkBlue.copy(alpha = 0.03f),
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = ElevatorGold,
                                    unfocusedIndicatorColor = ElevatorDarkBlue.copy(alpha = 0.15f),
                                    cursorColor = ElevatorDarkBlue
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = if (index < playerNames.size - 1) ImeAction.Next else ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) },
                                    onDone = { focusManager.clearFocus() }
                                ),
                                trailingIcon = {
                                    if (playerNames.size > 2) {
                                        IconButton(onClick = {
                                            val newList = playerNames.toMutableList()
                                            newList.removeAt(index)
                                            playerNames = newList
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Löschen",
                                                tint = Color.Red.copy(alpha = 0.5f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            TextButton(
                                onClick = { 
                                    focusManager.clearFocus()
                                    playerNames = playerNames + "" 
                                },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = ElevatorDarkBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Spieler Hinzufügen",
                                    color = ElevatorDarkBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Max Floor selection moved below Player List
                    Text(
                        "MAX. ETAGE",
                        color = ElevatorDarkBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(36.dp),
                    ) {
                        Slider(
                            value = maxFloor,
                            onValueChange = { maxFloor = it },
                            valueRange = 3f..15f,
                            steps = 11,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = ElevatorGold,
                                activeTrackColor = ElevatorGold,
                                inactiveTrackColor = ElevatorDarkBlue.copy(alpha = 0.1f)
                            )
                        )
                        Text(
                            "${maxFloor.toInt()}",
                            color = ElevatorDarkBlue,
                            modifier = Modifier.width(36.dp),
                            fontSize = 18.sp,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val isValid = playerNames.filter { it.isNotBlank() }.size >= 2

                    if (showError && !isValid) {
                        Text(
                            "Bitte mindestens 2 Spieler angeben!",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val filteredNames = playerNames.filter { it.isNotBlank() }
                            if (filteredNames.size >= 2) {
                                viewModel.startGame(filteredNames, maxFloor.toInt())
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier
                            .width(220.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isValid) ArtDecoGreen else ArtDecoGreen.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            "SPIEL STARTEN",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
