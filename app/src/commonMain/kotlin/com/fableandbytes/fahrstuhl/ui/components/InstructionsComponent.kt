package com.fableandbytes.fahrstuhl.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fableandbytes.fahrstuhl.ui.theme.*

@Composable
fun GameInstructionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Spielanleitung", color = ElevatorGold, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                item {
                    Text(
                        "Ziel des Spiels",
                        fontWeight = FontWeight.Bold,
                        color = ElevatorDarkBlue,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "In jeder Runde (Etage) musst du genau vorhersagen, wie viele Stiche du machen wirst. " +
                        "Wer seine Ansage genau trifft, erhält Punkte. Wer daneben liegt, erhält Minuspunkte.",
                        fontSize = 14.sp,
                        color = ElevatorDarkBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "Spielablauf",
                        fontWeight = FontWeight.Bold,
                        color = ElevatorDarkBlue,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "1. Ansage-Phase: Jeder Spieler gibt reihum seine Schätzung ab. " +
                        "Die Summe der Ansagen darf nicht genau der Etage entsprechen (der letzte Spieler ist eingeschränkt).\n\n" +
                        "2. Stich-Phase: Es wird gespielt. Am Ende werden die tatsächlich gemachten Stiche eingetragen.\n\n" +
                        "3. Punkte-Phase: Die Punkte werden berechnet und die nächste Etage beginnt.",
                        fontSize = 14.sp,
                        color = ElevatorDarkBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Die Etagen",
                        fontWeight = FontWeight.Bold,
                        color = ElevatorDarkBlue,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "Das Spiel beginnt bei Etage 1 (eine Karte pro Spieler), steigt bis zur Maximaletage an " +
                        "und fährt am Ende wieder im Fahrstuhl hinunter bis zur Etage 1.",
                        fontSize = 14.sp,
                        color = ElevatorDarkBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Punktevergabe",
                        fontWeight = FontWeight.Bold,
                        color = ElevatorDarkBlue,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "• Richtige Ansage (Treffer): Du erhältst 10 Basispunkte + (2 * Ansage).\n" +
                        "• Falsche Ansage (Zu wenig): Du erhältst 10 - (2 * Ansage).\n" +
                        "• Falsche Ansage (Zu viel): Du erhältst 10 - (2 * gemachte Stiche).\n\n" +
                        "Beispiel (bei Standardeinstellung):\n" +
                        "Ansage 0, gemacht 0 → 10 Punkte\n" +
                        "Ansage 1, gemacht 1 → 12 Punkte\n" +
                        "Ansage 1, gemacht 0 → 8 Punkte\n" +
                        "Ansage 2, gemacht 3 → 4 Punkte",
                        fontSize = 14.sp,
                        color = ElevatorDarkBlue
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Alles klar!", color = ElevatorDarkBlue, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = ElevatorCream
    )
}
