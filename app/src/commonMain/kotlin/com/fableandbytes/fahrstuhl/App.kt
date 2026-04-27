package com.fableandbytes.fahrstuhl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fableandbytes.fahrstuhl.ui.FahrstuhlApp
import com.fableandbytes.fahrstuhl.ui.SplashOverlay
import com.fableandbytes.fahrstuhl.ui.theme.ElevatorDarkBlue
import com.fableandbytes.fahrstuhl.ui.theme.FahrstuhlKartenspielTheme
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.viewmodel.SplashViewModel

@Composable
fun App(
    gameViewModel: GameViewModel,
    splashViewModel: SplashViewModel,
    onExportHistory: (String) -> Unit = {},
    onImportHistory: () -> Unit = {}
) {
    FahrstuhlKartenspielTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = ElevatorDarkBlue
            ) { innerPadding ->
                FahrstuhlApp(
                    viewModel = gameViewModel,
                    modifier = Modifier.padding(innerPadding),
                    onExportHistory = onExportHistory,
                    onImportHistory = onImportHistory
                )
            }

            SplashOverlay(isVisible = splashViewModel.showOverlay)
        }
    }
}
