package com.fableandbytes.fahrstuhl.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fableandbytes.fahrstuhl.ui.theme.*

@Composable
fun ElevatorLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        // Arrows: Up and Down
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = ElevatorGold,
                modifier = Modifier.size(24.dp)
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ElevatorGold,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Invisible spacer to balance the buttons on the right,
            // ensuring the door itself is centered under the arrows.
            Spacer(modifier = Modifier.width(12.dp))

            // Elevator Door Frame
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 70.dp)
                    .border(3.dp, ElevatorGold, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .padding(4.dp)
            ) {
                // Interior Door Detail
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top indicator bar inside frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(ElevatorGold.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Door
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ElevatorGold, ElevatorGold.copy(alpha = 0.7f))
                                    )
                                )
                                .border(0.5.dp, ElevatorDarkBlue.copy(alpha = 0.3f))
                        )
                        // Middle Gap
                        Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(ElevatorDarkBlue.copy(alpha = 0.5f)))
                        // Right Door
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ElevatorGold.copy(alpha = 0.7f), ElevatorGold)
                                    )
                                )
                                .border(0.5.dp, ElevatorDarkBlue.copy(alpha = 0.3f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Side Buttons
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(6.dp).background(ElevatorGold, CircleShape))
                Box(modifier = Modifier.size(6.dp).background(ElevatorGold, CircleShape))
            }
        }
    }
}

@Composable
fun ElevatorIndicator(
    floor: Int,
    onUpClick: () -> Unit = {},
    onDownClick: () -> Unit = {},
    canGoUp: Boolean = false,
    canGoDown: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onUpClick, enabled = canGoUp) {
            Icon(
                Icons.Default.KeyboardArrowUp, 
                contentDescription = "Etage vorwärts", 
                tint = if (canGoUp) ElevatorGold else ElevatorGold.copy(alpha = 0.3f), 
                modifier = Modifier.size(44.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(110.dp)
                .border(4.dp, ElevatorGold, RoundedCornerShape(20.dp))
                .background(ElevatorCream),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    "ETAGE", 
                    color = ElevatorDarkBlue, 
                    style = MaterialTheme.typography.labelMedium, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Box(
                    modifier = Modifier.height(65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = floor,
                        transitionSpec = {
                            if (targetState > initialState) {
                                // Upward movement (new floor is higher)
                                (slideInVertically { height -> height } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                            } else {
                                // Downward movement
                                (slideInVertically { height -> -height } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> height } + fadeOut())
                            }.using(
                                SizeTransform(clip = false)
                            )
                        }, label = "FloorAnimation"
                    ) { targetFloor ->
                        Text(
                            "$targetFloor",
                            color = ElevatorDarkBlue,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        IconButton(onClick = onDownClick, enabled = canGoDown) {
            Icon(
                Icons.Default.KeyboardArrowDown, 
                contentDescription = "Etage zurück", 
                tint = if (canGoDown) ElevatorGold else ElevatorGold.copy(alpha = 0.3f), 
                modifier = Modifier.size(44.dp)
            )
        }
    }
}
