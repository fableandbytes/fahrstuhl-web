package com.fableandbytes.fahrstuhl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.fableandbytes.fahrstuhl.ui.theme.*

val ElevatorCardShape = GenericShape { size, _ ->
    val nW = size.width * 0.52f // Width of the notch (increased from 0.42)
    val nH = 180f // Depth of the notch
    val r = 120f // Main corner radius
    val notchR = 60f // Smooth curve for the notch shoulders
    
    val cX = size.width / 2
    
    moveTo(0f, r)
    // Top left corner
    arcTo(Rect(0f, 0f, r * 2, r * 2), 180f, 90f, false)
    
    // Line to notch shoulder
    lineTo(cX - nW/2 - notchR, 0f)
    
    // Notch shoulder left (curves down)
    arcTo(Rect(cX - nW/2 - notchR, 0f, cX - nW/2 + notchR, notchR * 2), 270f, 90f, false)
    
    // Down into the notch
    lineTo(cX - nW/2 + notchR, nH - notchR)
    
    // Notch bottom curve left
    arcTo(Rect(cX - nW/2 + notchR, nH - notchR * 2, cX - nW/2 + notchR * 3, nH), 180f, -90f, false)
    
    // Bottom of the notch
    lineTo(cX + nW/2 - notchR * 3, nH)
    
    // Notch bottom curve right
    arcTo(Rect(cX + nW/2 - notchR * 3, nH - notchR * 2, cX + nW/2 - notchR, nH), 90f, -90f, false)
    
    // Up to notch shoulder right
    lineTo(cX + nW/2 - notchR, notchR)
    
    // Notch shoulder right (curves back to top line)
    arcTo(Rect(cX + nW/2 - notchR, 0f, cX + nW/2 + notchR, notchR * 2), 180f, 90f, false)
    
    // Top right corner
    lineTo(size.width - r, 0f)
    arcTo(Rect(size.width - r * 2, 0f, size.width, r * 2), 270f, 90f, false)
    
    // Bottom right corner
    lineTo(size.width, size.height - 80f)
    arcTo(Rect(size.width - 160f, size.height - 160f, size.width, size.height), 0f, 90f, false)
    
    // Bottom left corner
    lineTo(80f, size.height)
    arcTo(Rect(0f, size.height - 160f, 160f, size.height), 90f, 90f, false)
    
    close()
}

@Composable
fun DecorativeBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ElevatorDarkBlue)
    ) {
        // Center stripe
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(130.dp)
                .align(Alignment.Center)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.2f),
                            ElevatorDarkBlue,
                            Color.Black.copy(alpha = 0.2f)
                        )
                    )
                )
        )

        // Elevator Logo centered above the notch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            ElevatorLogo()
        }

        // Main Cream Shape (The "Card") with Notch
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 95.dp, bottom = 20.dp, start = 12.dp, end = 12.dp)
                .clip(ElevatorCardShape)
                .background(ElevatorCream)
                .border(
                    width = 4.dp,
                    brush = Brush.verticalGradient(listOf(ElevatorGold, ElevatorHighlight, ElevatorGold)),
                    shape = ElevatorCardShape
                )
        )

        content()
    }
}
