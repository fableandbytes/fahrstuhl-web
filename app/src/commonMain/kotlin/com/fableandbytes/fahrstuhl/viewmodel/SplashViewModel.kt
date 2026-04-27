package com.fableandbytes.fahrstuhl.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    // Steuert den nativen Android Splash Screen (System)
    var keepSplashScreen by mutableStateOf(true)
        private set

    // Steuert das Compose-Overlay (Branding & Progress)
    var showOverlay by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            // 1. Native Splash Screen SOFORT beenden, sobald die App geladen ist
            delay(50) 
            keepSplashScreen = false
            
            // 2. Das Compose-Overlay (Branding & Progress) für eine feste Zeit zeigen
            // Wir erhöhen auf 3 Sekunden für einen premium Eindruck
            delay(3000)
            showOverlay = false
        }
    }
}
