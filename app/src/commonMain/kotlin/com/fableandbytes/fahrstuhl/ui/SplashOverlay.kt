package com.fableandbytes.fahrstuhl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import fahrstuhlkartenspiel.app.generated.resources.Res
import fahrstuhlkartenspiel.app.generated.resources.ic_launcher_foreground
import fahrstuhlkartenspiel.app.generated.resources.branding_text

@Composable
fun SplashOverlay(
    isVisible: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(durationMillis = 800))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1221)), // ElevatorDarkBlue
            contentAlignment = Alignment.Center
        ) {
            // Das Icon ist EXAKT in der Mitte, wie beim System-Splash
            Image(
                painter = painterResource(Res.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            // Der Ladekreis wird um das Icon herum oder leicht versetzt platziert,
            // ohne die Position des Icons zu verschieben.
            CircularProgressIndicator(
                modifier = Modifier.size(140.dp), // Etwas größer als das Icon
                color = Color(0xFFF6E7D2),
                strokeWidth = 2.dp
            )

            // Branding Logo am unteren Rand (wieder auf branding_text zurückgestellt)
            Image(
                painter = painterResource(Res.drawable.branding_text),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
                    .width(220.dp)
            )
        }
    }
}
