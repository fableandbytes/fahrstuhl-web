package com.fableandbytes.fahrstuhl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fableandbytes.fahrstuhl.model.GameHistoryEntry
import com.fableandbytes.fahrstuhl.ui.theme.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun HistoryDialog(
    history: List<GameHistoryEntry>, 
    onDismiss: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onExport: () -> Unit = {},
    onImport: () -> Unit = {}
) {
    var selectedEntry by remember { mutableStateOf<GameHistoryEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<GameHistoryEntry?>(null) }

    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Spiel löschen?", color = ElevatorGold, fontWeight = FontWeight.Bold) },
            text = { Text("Möchtest du diesen Eintrag wirklich dauerhaft aus dem Logbuch entfernen?", color = ElevatorDarkBlue) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteEntry(entryToDelete!!.date)
                    entryToDelete = null
                }) {
                    Text("Löschen", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Abbrechen", color = ElevatorDarkBlue)
                }
            },
            containerColor = ElevatorCream
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (selectedEntry == null) "Logbuch vergangener Spiele" else "Spieldetails", 
                color = ElevatorGold, 
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            if (selectedEntry == null) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onExport,
                            enabled = history.isNotEmpty(),
                            modifier = Modifier.weight(1f).height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElevatorDarkBlue.copy(alpha = 0.1f),
                                disabledContainerColor = ElevatorDarkBlue.copy(alpha = 0.02f)
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Sync: Teilen", 
                                fontSize = 11.sp, 
                                color = if (history.isNotEmpty()) ElevatorDarkBlue else ElevatorDarkBlue.copy(alpha = 0.3f), 
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = onImport,
                            modifier = Modifier.weight(1f).height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatorDarkBlue.copy(alpha = 0.1f)),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Sync: Import", fontSize = 11.sp, color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (history.isEmpty()) {
                        Text("Noch keine Spiele aufgezeichnet.", color = ElevatorDarkBlue)
                    } else {
                        val allPlayerNames = remember(history) { history.flatMap { it.playerNames }.distinct().sorted() }
                        var visiblePlayers by remember { mutableStateOf(allPlayerNames.toSet()) }
                        var showFilterDialog by remember { mutableStateOf(false) }

                        val stats = allPlayerNames
                            .filter { it in visiblePlayers }
                            .map { name ->
                                val gamesPlayed = history.count { name in it.playerNames }
                                val wins = history.count { it.winnerName == name }
                                name to (wins to gamesPlayed)
                            }.sortedByDescending { it.second.first }

                        if (showFilterDialog) {
                            AlertDialog(
                                onDismissRequest = { showFilterDialog = false },
                                title = { Text("Spieler filtern", color = ElevatorGold, fontWeight = FontWeight.Bold) },
                                text = {
                                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                                        items(allPlayerNames) { name ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        visiblePlayers = if (name in visiblePlayers) {
                                                            visiblePlayers - name
                                                        } else {
                                                            visiblePlayers + name
                                                        }
                                                    }
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = name in visiblePlayers,
                                                    onCheckedChange = null,
                                                    colors = CheckboxDefaults.colors(checkedColor = ElevatorGold)
                                                )
                                                Text(name, color = ElevatorDarkBlue, modifier = Modifier.padding(start = 8.dp))
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showFilterDialog = false }) {
                                        Text("Fertig", color = ElevatorDarkBlue)
                                    }
                                },
                                containerColor = ElevatorCream
                            )
                        }

                        Surface(
                            modifier = Modifier.clickable { showFilterDialog = true },
                            color = ElevatorDarkBlue.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ElevatorGold.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("SIEGE & SPIELE INSGESAMT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ElevatorGold)
                                    Text("Filter ⚙", fontSize = 10.sp, color = ElevatorGold.copy(alpha = 0.7f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                if (stats.isEmpty()) {
                                    Text("Keine Spieler ausgewählt", fontSize = 12.sp, color = ElevatorDarkBlue.copy(alpha = 0.5f))
                                } else {
                                    stats.forEach { (name, winData) ->
                                        val (wins, played) = winData
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(name, fontSize = 13.sp, color = ElevatorDarkBlue, fontWeight = FontWeight.Medium)
                                            Text("$wins Siege / $played Spiele", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ArtDecoGreen)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                            items(history, key = { it.date }) { entry ->
                                val date = Instant.fromEpochMilliseconds(entry.date)
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                val dateStr = "${date.dayOfMonth.toString().padStart(2, '0')}.${date.monthNumber.toString().padStart(2, '0')}.${date.year} " +
                                             "${date.hour.toString().padStart(2, '0')}:${date.minute.toString().padStart(2, '0')}"
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .pointerInput(entry) {
                                            detectTapGestures(
                                                onLongPress = { entryToDelete = entry },
                                                onTap = { selectedEntry = entry }
                                            )
                                        },
                                    colors = CardDefaults.cardColors(containerColor = ElevatorDarkBlue.copy(alpha = 0.05f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(dateStr, fontSize = 12.sp, color = ElevatorDarkBlue.copy(alpha = 0.6f))
                                            Text("Gewinner: ${entry.winnerName}", fontWeight = FontWeight.Bold, color = ArtDecoGreen)
                                        }
                                        val maxFloorReached = entry.rounds.maxOfOrNull { it.floor } ?: 0
                                        Text("Max. Etage: $maxFloorReached", fontSize = 11.sp, color = ElevatorGold, fontWeight = FontWeight.Bold)
                                        Text("Spieler: ${entry.playerNames.joinToString(", ")}", fontSize = 14.sp, color = ElevatorDarkBlue)
                                        Text("Punkte: ${entry.winnerScore}", fontSize = 14.sp, color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
                                        Text("Details ansehen >", fontSize = 10.sp, color = ElevatorGold, modifier = Modifier.align(Alignment.End))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val entry = selectedEntry!!
                Column {
                    val date = Instant.fromEpochMilliseconds(entry.date)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    val dateStr = "${date.dayOfMonth.toString().padStart(2, '0')}.${date.monthNumber.toString().padStart(2, '0')}.${date.year} " +
                                 "${date.hour.toString().padStart(2, '0')}:${date.minute.toString().padStart(2, '0')}"
                    Text(dateStr, fontSize = 12.sp, color = ElevatorDarkBlue.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
                        items(entry.rounds) { round ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = ElevatorDarkBlue.copy(alpha = 0.05f)),
                                border = if (round.floor == 1) BorderStroke(1.dp, ElevatorGold) else null
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("ETAGE ${round.floor}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = ElevatorGold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    entry.playerNames.forEachIndexed { index, name ->
                                        val pred = round.predictions[index] ?: 0
                                        val res = round.results[index] ?: 0
                                        val score = round.scores[index] ?: 0
                                        val total = round.cumulativeScores[index] ?: 0
                                        
                                        val rank = round.cumulativeScores.values.count { it > total } + 1
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("$rank. $name", modifier = Modifier.weight(1f), fontSize = 13.sp, color = ElevatorDarkBlue, fontWeight = FontWeight.Medium)
                                            Text("$pred → $res", fontSize = 12.sp, color = ElevatorDarkBlue.copy(alpha = 0.7f), modifier = Modifier.padding(horizontal = 8.dp))
                                            Text("$total ", 
                                                fontSize = 12.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                color = ElevatorDarkBlue
                                            )
                                            Text("(${if(score >= 0) "+" else ""}$score)", 
                                                fontSize = 12.sp, 
                                                fontWeight = FontWeight.Normal,
                                                color = if(score >= 10) ArtDecoGreen else Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val finalScores = entry.rounds.lastOrNull()?.cumulativeScores ?: emptyMap()
                    val ranking = entry.playerNames.mapIndexed { index, name ->
                        name to (finalScores[index] ?: 0)
                    }.sortedByDescending { it.second }

                    Surface(
                        color = ElevatorDarkBlue.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElevatorGold.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                            Text("SIEGEREHRUNG", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ElevatorGold, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            ranking.take(3).forEachIndexed { index, pair ->
                                val color = when(index) {
                                    0 -> ElevatorGold
                                    1 -> Color.Gray
                                    2 -> Color(0xFFCD7F32) // Bronze
                                    else -> ElevatorDarkBlue
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${index + 1}.", fontWeight = FontWeight.Black, color = color, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(pair.first, fontWeight = FontWeight.Bold, color = ElevatorDarkBlue, fontSize = 13.sp)
                                    }
                                    Text("${pair.second} Pkt.", fontWeight = FontWeight.Bold, color = ArtDecoGreen, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedEntry != null) {
                TextButton(onClick = { selectedEntry = null }) {
                    Text("Zurück", color = ElevatorDarkBlue)
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Schließen", color = ElevatorDarkBlue)
                }
            }
        },
        containerColor = ElevatorCream
    )
}
