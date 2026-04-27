package com.fableandbytes.fahrstuhl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import fahrstuhlkartenspiel.app.generated.resources.Res
import fahrstuhlkartenspiel.app.generated.resources.branding_text
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.ui.theme.*
import com.fableandbytes.fahrstuhl.ui.components.*

@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showInstructions by remember { mutableStateOf(false) }

    BackHandler {
        if (showInstructions) showInstructions = false else viewModel.goBack()
    }

    if (showInstructions) {
        GameInstructionsDialog(onDismiss = { showInstructions = false })
    }

    DecorativeBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(180.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.goBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = ElevatorDarkBlue)
                }
                Text(
                    "Einstellungen",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElevatorDarkBlue,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    ScoringSettingItem(
                        label = "Basispunkte pro Runde",
                        value = uiState.basePointsCorrect,
                        onValueChange = { viewModel.updateScoringSettings(it, uiState.pointsPerStichCorrect, uiState.minusPointsPerStichDiff) }
                    )
                    
                    HorizontalDivider(color = ElevatorDarkBlue.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                    ScoringSettingItem(
                        label = "Multiplikator für Stiche",
                        value = uiState.pointsPerStichCorrect,
                        onValueChange = { viewModel.updateScoringSettings(uiState.basePointsCorrect, it, uiState.minusPointsPerStichDiff) },
                        step = 1
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { viewModel.startReordering() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElevatorDarkBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Reorder, contentDescription = null, tint = ElevatorGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SITZORDNUNG ÄNDERN", color = ElevatorGold, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { showInstructions = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElevatorDarkBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = ElevatorGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SPIELANLEITUNG", color = ElevatorGold, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Das Punktesystem ist nun fest auf die Standardregeln eingestellt.",
                color = ElevatorGold, 
                fontSize = 12.sp, 
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(Res.drawable.branding_text),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .width(180.dp)
            )
        }
    }
}

@Composable
fun ScoringSettingItem(label: String, value: Int, onValueChange: (Int) -> Unit, step: Int = 5) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = ElevatorDarkBlue.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "$value",
                color = ElevatorDarkBlue,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(60.dp)
            )
            Row {
                FilledIconButton(
                    onClick = { onValueChange(value - step) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = ElevatorDarkBlue),
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("-", color = ElevatorGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    onClick = { onValueChange(value + step) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = ElevatorDarkBlue),
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("+", color = ElevatorGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }
}
