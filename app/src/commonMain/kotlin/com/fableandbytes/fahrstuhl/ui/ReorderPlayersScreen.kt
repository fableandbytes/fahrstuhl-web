package com.fableandbytes.fahrstuhl.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.ui.theme.*
import com.fableandbytes.fahrstuhl.ui.components.*

@Composable
fun ReorderPlayersScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var players by remember { mutableStateOf(uiState.players) }
    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current

    // Drag and Drop State
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffset by remember { mutableStateOf(0f) }

    // Constants for calculation
    val itemHeight = 64.dp
    val spacing = 12.dp
    val fullItemHeightPx = with(density) { (itemHeight + spacing).toPx() }

    BackHandler {
        viewModel.goBack()
    }

    DecorativeBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 40.dp),
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
                    "Sitzordnung",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElevatorDarkBlue,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Text(
                "Halte einen Spieler gedrückt, um ihn zu verschieben.",
                color = ElevatorDarkBlue.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                lazyListState.layoutInfo.visibleItemsInfo
                                    .firstOrNull { item ->
                                        offset.y.toInt() in item.offset..(item.offset + item.size)
                                    }
                                    ?.let { item ->
                                        draggedItemIndex = item.index
                                    }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                draggingOffset += dragAmount.y
                                
                                val currentIdx = draggedItemIndex ?: return@detectDragGesturesAfterLongPress
                                
                                // Drag down
                                if (draggingOffset > fullItemHeightPx / 2f && currentIdx < players.size - 1) {
                                    players = players.toMutableList().apply {
                                        val item = removeAt(currentIdx)
                                        add(currentIdx + 1, item)
                                    }
                                    draggedItemIndex = currentIdx + 1
                                    draggingOffset -= fullItemHeightPx
                                } 
                                // Drag up
                                else if (draggingOffset < -fullItemHeightPx / 2f && currentIdx > 0) {
                                    players = players.toMutableList().apply {
                                        val item = removeAt(currentIdx)
                                        add(currentIdx - 1, item)
                                    }
                                    draggedItemIndex = currentIdx - 1
                                    draggingOffset += fullItemHeightPx
                                }
                            },
                            onDragEnd = {
                                draggedItemIndex = null
                                draggingOffset = 0f
                            },
                            onDragCancel = {
                                draggedItemIndex = null
                                draggingOffset = 0f
                            }
                        )
                    },
                verticalArrangement = Arrangement.spacedBy(spacing),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(players, key = { _, player -> player.id }) { index, player ->
                    val isDragging = index == draggedItemIndex
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 2.dp)
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer {
                                translationY = if (isDragging) draggingOffset else 0f
                                scaleX = if (isDragging) 1.03f else 1f
                                scaleY = if (isDragging) 1.03f else 1f
                            }
                            .shadow(elevation, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isDragging) 2.dp else 1.dp, 
                                color = if (isDragging) ArtDecoGreen else ElevatorDarkBlue.copy(alpha = 0.1f), 
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = Color.White
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                "${index + 1}.",
                                color = ElevatorDarkBlue.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(30.dp)
                            )
                            
                            Text(
                                player.name,
                                color = ElevatorDarkBlue,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                Icons.Default.DragHandle,
                                contentDescription = null,
                                tint = if (isDragging) ArtDecoGreen else ElevatorDarkBlue.copy(alpha = 0.3f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    viewModel.updatePlayerOrder(players)
                    viewModel.goBack() 
                },
                modifier = Modifier
                    .width(220.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArtDecoGreen),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    "REIHENFOLGE SPEICHERN",
                    fontSize = 13.sp,
                    color = Color.White, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
