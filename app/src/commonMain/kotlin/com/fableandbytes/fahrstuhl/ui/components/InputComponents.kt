package com.fableandbytes.fahrstuhl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import fahrstuhlkartenspiel.app.generated.resources.Res
import fahrstuhlkartenspiel.app.generated.resources.crown
import com.fableandbytes.fahrstuhl.model.GamePhase
import com.fableandbytes.fahrstuhl.model.Player
import com.fableandbytes.fahrstuhl.ui.theme.*

@Composable
fun CrownIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.crown),
        contentDescription = "Führender",
        modifier = modifier
            .size(24.dp)
            .rotate(15f)
    )
}

@Composable
fun PlayerGameRow(
    player: Player,
    rank: Int,
    isStartPlayer: Boolean,
    prediction: Int?,
    result: Int?,
    onPredictionChange: (Int) -> Unit,
    onResultChange: (Int) -> Unit,
    maxVal: Int,
    forbiddenValue: Int?,
    phase: GamePhase,
    modifier: Modifier = Modifier
) {
    val isPredictionPhase = phase == GamePhase.PREDICTION
    val isResultPhase = phase == GamePhase.RESULT

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = ElevatorDarkBlue),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, ElevatorGold)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$rank.",
                color = ElevatorGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(28.dp)
            )

            Column(
                modifier = Modifier.weight(1.3f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isStartPlayer) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Startspieler",
                            tint = ElevatorGold,
                            modifier = Modifier.size(12.dp).padding(end = 2.dp)
                        )
                    }
                    Box(contentAlignment = Alignment.TopEnd) {
                        Text(
                            text = player.name,
                            color = ElevatorCream,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = if (rank == 1) 12.dp else 0.dp)
                        )
                        if (rank == 1 && player.totalScore > 0) {
                            CrownIcon(
                                modifier = Modifier
                                    .offset(y = (-14).dp, x = 12.dp)
                            )
                        }
                    }
                }
                Text("${player.totalScore} Pkt", color = ElevatorGold.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Prediction Column
            InputColumn(
                label = "ANSAGE",
                value = prediction,
                onValueChange = onPredictionChange,
                maxVal = maxVal,
                forbiddenValue = forbiddenValue,
                enabled = isPredictionPhase
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Result Column
            InputColumn(
                label = "STICHE",
                value = result,
                onValueChange = onResultChange,
                maxVal = maxVal,
                enabled = isResultPhase
            )
        }
    }
}

@Composable
fun RowScope.InputColumn(
    label: String,
    value: Int?,
    onValueChange: (Int) -> Unit,
    maxVal: Int,
    forbiddenValue: Int? = null,
    enabled: Boolean
) {
    val isForbidden = value != null && value == forbiddenValue
    val canAdd = (value ?: 0) < maxVal
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
        Text(label, color = ElevatorCream, style = MaterialTheme.typography.labelSmall)
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (enabled) ElevatorCream else ElevatorCream.copy(alpha = 0.5f))
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = { 
                        if (value == null) onValueChange(0) 
                        else onValueChange(value - 1) 
                    },
                    enabled = enabled && (value == null || value > 0),
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("-", color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
                }
                
                Text(
                    text = value?.toString() ?: "-",
                    color = if (isForbidden) Color.Red else ElevatorDarkBlue,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = { onValueChange((value ?: 0) + 1) },
                    enabled = enabled && canAdd,
                    modifier = Modifier.size(28.dp)
                ) {
                    Text(
                        "+", 
                        color = if (enabled && canAdd) ElevatorDarkBlue else ElevatorDarkBlue.copy(alpha = 0.3f), 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (isForbidden) {
                Text(
                    "!",
                    color = Color.Red,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-10).dp)
                )
            }
        }
    }
}
