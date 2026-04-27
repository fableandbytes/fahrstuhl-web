package com.fableandbytes.fahrstuhl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fableandbytes.fahrstuhl.data.appContext
import com.fableandbytes.fahrstuhl.viewmodel.GameViewModel
import com.fableandbytes.fahrstuhl.viewmodel.SplashViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        appContext = applicationContext
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.keepSplashScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        handleIntent(intent)

        setContent {
            // Wir rufen die zentrale App-Funktion aus commonMain auf
            App(
                gameViewModel = viewModel,
                splashViewModel = splashViewModel,
                onExportHistory = { json ->
                    Toast.makeText(this@MainActivity, "Export wird vorbereitet...", Toast.LENGTH_SHORT).show()
                    // shareHistory Logik hierher verschieben oder als Action übergeben
                },
                onImportHistory = { importLauncher.launch("*/*") }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        // ... (Intent Logik bleibt hier, da Android-spezifisch)
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { importFromUri(it) }
    }

    private fun importFromUri(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val json = reader.readText()
                viewModel.importHistory(json) { success ->
                    val msg = if (success) "Erfolgreich importiert!" else "Fehler beim Import."
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Datei konnte nicht gelesen werden.", Toast.LENGTH_LONG).show()
        }
    }
}
