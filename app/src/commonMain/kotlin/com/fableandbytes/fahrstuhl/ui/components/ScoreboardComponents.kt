package com.fableandbytes.fahrstuhl.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import fahrstuhlkartenspiel.app.generated.resources.Res
import fahrstuhlkartenspiel.app.generated.resources.crown
import com.fableandbytes.fahrstuhl.model.Achievement
import com.fableandbytes.fahrstuhl.model.Player
import com.fableandbytes.fahrstuhl.model.Round
import com.fableandbytes.fahrstuhl.ui.theme.*

@Composable
fun PodiumPlace(players: List<Player>, height: androidx.compose.ui.unit.Dp, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(95.dp)
    ) {
        players.forEach { player ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    player.name,
                    color = ElevatorDarkBlue,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                if (player.achievements.isNotEmpty()) {
                    Row(modifier = Modifier.padding(bottom = 2.dp)) {
                        player.achievements.take(3).forEach { achievement ->
                            Icon(
                                imageVector = achievement.icon,
                                contentDescription = achievement.title,
                                tint = ElevatorGold.copy(alpha = 0.8f),
                                modifier = Modifier.size(12.dp).padding(horizontal = 1.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(color.copy(alpha = 0.9f))
                    .border(1.dp, ElevatorGold.copy(alpha = 0.4f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.95f),
                                color.copy(alpha = 0.7f),
                                color.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(2.dp, ElevatorGold.copy(alpha = 0.3f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
                contentAlignment = Alignment.Center
            ) {
                val score = players.firstOrNull()?.totalScore ?: 0
                Text(
                    if (score >= 0) "+$score" else "$score",
                    color = ElevatorDarkBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun ScoreboardTable(
    players: List<Player>,
    rounds: List<Round>,
    basePoints: Int,
    onFloorClick: () -> Unit,
    onCellClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .border(2.dp, ElevatorDarkBlue, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(Color.White)
            .horizontalScroll(horizontalScrollState)
    ) {
        // ... (Header bleibt gleich)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ElevatorDarkBlue.copy(alpha = 0.95f))
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Etage",
                modifier = Modifier.width(45.dp),
                color = ElevatorGold,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
            players.forEach { player ->
                Column(
                    modifier = Modifier.width(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        player.name,
                        color = ElevatorGold,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        fontSize = 12.sp
                    )
                    if (player.achievements.isNotEmpty()) {
                        Row {
                            player.achievements.forEach { achievement ->
                                Icon(
                                    imageVector = achievement.icon,
                                    contentDescription = achievement.title,
                                    tint = ElevatorGold.copy(alpha = 0.7f),
                                    modifier = Modifier.size(10.dp).padding(horizontal = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(rounds.reversed()) { round ->
                Column {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(45.dp)
                                .fillMaxHeight()
                                .background(ElevatorDarkBlue.copy(alpha = 0.85f))
                                .clickable { onFloorClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val totalPredictions = round.predictions.values.sum()
                                if (totalPredictions > round.floor) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropUp,
                                        contentDescription = "Überboten",
                                        tint = Color.Red.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                                    )
                                } else if (totalPredictions < round.floor) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Unterboten",
                                        tint = ArtDecoGreen.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                Text("${round.floor}", color = ElevatorGold, fontWeight = FontWeight.Bold)
                            }
                        }

                        players.forEachIndexed { index, player ->
                            val roundScore = round.scores[player.id] ?: 0
                            val cumulativeScore = round.cumulativeScores[player.id] ?: 0
                            val pred = round.predictions[player.id] ?: 0
                            val res = round.results[player.id] ?: 0

                            val maxRoundScore = round.cumulativeScores.values.maxOrNull() ?: 0
                            val isLeader = cumulativeScore == maxRoundScore && cumulativeScore > 0

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .fillMaxHeight()
                                    .clickable { onCellClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        "$pred → $res",
                                        fontSize = 10.sp,
                                        color = ElevatorDarkBlue.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isLeader) {
                                            Image(
                                                painter = painterResource(Res.drawable.crown),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .offset(y = (-1).dp)
                                                    .padding(end = 4.dp)
                                            )
                                        }
                                        Text(
                                            if (cumulativeScore >= 0) "+$cumulativeScore" else "$cumulativeScore",
                                            color = if (cumulativeScore >= 0) ElevatorDarkBlue else Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Text(
                                        "(${if (roundScore >= 0) "+" else ""}$roundScore)",
                                        fontSize = 10.sp,
                                        color = if (roundScore >= basePoints) ArtDecoGreen.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Normal
                                    )
                                }

                                if (index < players.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .width(1.dp)
                                            .fillMaxHeight(0.6f)
                                            .background(ElevatorDarkBlue.copy(alpha = 0.08f))
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = ElevatorDarkBlue.copy(alpha = 0.1f), thickness = 1.dp)
                }
            }
        }
    }
}
