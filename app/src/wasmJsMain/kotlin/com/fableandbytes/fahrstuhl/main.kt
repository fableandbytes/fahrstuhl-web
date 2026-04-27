package com.fableandbytes.fahrstuhl

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.viewmodel.SplashViewModel
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val gameViewModel = GameViewModel()
    val splashViewModel = SplashViewModel()
    
    ComposeViewport(document.body!!) {
        App(
            gameViewModel = gameViewModel,
            splashViewModel = splashViewModel,
            onExportHistory = { /* Web-spezifischer Export */ },
            onImportHistory = { /* Web-spezifischer Import */ }
        )
    }
}
