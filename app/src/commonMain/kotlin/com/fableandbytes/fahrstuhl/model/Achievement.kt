package com.fableandbytes.fahrstuhl.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val flavorText: String,
    @Transient
    val icon: ImageVector = Icons.Default.Star,
    val category: AchievementCategory = AchievementCategory.SPECIAL
)

enum class AchievementCategory {
    PRECISION, RISK, SLIP_UP, BEHAVIOR, STRATEGY, SPECIAL
}

object AchievementRegistry {
    // 1. Die Präzisions-Klasse
    val SCHWEIZER_LIFTFUEHRER = Achievement(
        "schweizer_liftfuehrer",
        "Der Schweizer Liftführer",
        "100% Trefferquote",
        "Präziser als ein Uhrwerk. Bleibt nie zwischen den Stockwerken stecken.",
        Icons.Default.Verified,
        AchievementCategory.PRECISION
    )
    val ERFAHRENER_CONCIERGE = Achievement(
        "erfahrener_concierge",
        "Der erfahrene Concierge",
        "> 80% Trefferquote",
        "Kennt seine Gäste und seine Karten. Fast immer eine Punktlandung.",
        Icons.Default.Person,
        AchievementCategory.PRECISION
    )
    val WACKELKONTAKT = Achievement(
        "wackelkontakt",
        "Wackelkontakt im Taster",
        "50% - 79% Trefferquote",
        "Manchmal drückt er die 4 und landet in der 5. Etage. Aber meistens passt's.",
        Icons.Default.Bolt,
        AchievementCategory.PRECISION
    )
    val TECHNISCHER_DEFEKT = Achievement(
        "technischer_defekt",
        "Technischer Defekt",
        "< 30% Trefferquote",
        "Dieser Aufzug hält wo er will, nur nicht da, wo man gedrückt hat.",
        Icons.Default.Error,
        AchievementCategory.PRECISION
    )

    // 2. Die Risiko-Klasse
    val PENTHOUSE_STUERMER = Achievement(
        "penthouse_stuermer",
        "Penthouse-Stürmer",
        "8+ Stiche angesagt und geschafft",
        "Ganz oben ist die Luft dünn, aber die Aussicht ist herrlich!",
        Icons.Default.Apartment,
        AchievementCategory.RISK
    )
    val KELLERKIND = Achievement(
        "kellerkind",
        "Kellerkind",
        "> 70% '0' angesagt",
        "Traut sich nicht aus dem Souterrain raus. Es ist dort so schön sicher.",
        Icons.Default.South,
        AchievementCategory.RISK
    )
    val VORSICHTIGER_PORTIER = Achievement(
        "vorsichtiger_portier",
        "Der vorsichtige Portier",
        "Viele 0er angesagt UND alle exakt gehalten",
        "Sicherheit geht vor. Er lässt niemanden rein, den er nicht kennt.",
        Icons.Default.Lock,
        AchievementCategory.RISK
    )
    val FAHRSTUHL_ROWDY = Achievement(
        "fahrstuhl_rowdy",
        "Fahrstuhl-Rowdy",
        "Oft hoch angesagt, meistens daneben",
        "Drückt im Aufzug alle Knöpfe gleichzeitig und hofft das Beste.",
        Icons.Default.Warning,
        AchievementCategory.RISK
    )

    // 3. Die "Ausrutscher"
    val FREIER_FALL = Achievement(
        "freier_fall",
        "Im freien Fall",
        "5+ angesagt, 0 gemacht",
        "Das Seil ist gerissen. Wir sehen uns ganz unten im Schacht.",
        Icons.Default.SouthEast,
        AchievementCategory.SLIP_UP
    )
    val UEBERBELEGUNG = Achievement(
        "ueberbelegung",
        "Überbelegungs-Alarm",
        "3+ Stiche mehr gemacht als angesagt",
        "Halt! Zu viele Leute im Korb! Dieser Stich war nicht geplant.",
        Icons.Default.People,
        AchievementCategory.SLIP_UP
    )
    val GESCHLOSSENE_TUER = Achievement(
        "geschlossene_tuer",
        "Die geschlossene Tür",
        "1 angesagt, 0 gemacht",
        "Knapp vorbei ist auch daneben. Die Tür ging vor der Nase zu.",
        Icons.Default.SensorDoor,
        AchievementCategory.SLIP_UP
    )
    val STECKENGEBLIEBEN = Achievement(
        "steckengeblieben",
        "Steckengeblieben",
        "In der letzten Runde Sieg verloren",
        "Kurz vor dem Ziel gab es einen Stromausfall. Tragisch.",
        Icons.Default.Pause,
        AchievementCategory.SLIP_UP
    )

    // 4. Die Verhaltens-Spezialisten
    val EWIGER_ZWEITE = Achievement(
        "ewiger_zweite",
        "Der ewige Zweite",
        "Meiste Zeit auf Platz 2, nie auf Platz 1",
        "Immer im Schatten des Penthouses. Ein solider Vize-Direktor.",
        Icons.Default.FormatListNumbered,
        AchievementCategory.BEHAVIOR
    )
    val EXPRESS_LIFT = Achievement(
        "express_lift",
        "Express-Lift",
        "Größter Punktesprung in einer Runde",
        "Von ganz unten nach ganz oben in Rekordzeit. Bitte Ohren zuhalten!",
        Icons.Default.Speed,
        AchievementCategory.BEHAVIOR
    )
    val BODENSTATION = Achievement(
        "bodenstation",
        "Bodenstation",
        "Ganzes Spiel auf dem letzten Platz",
        "Jemand muss ja unten aufpassen, dass das Gebäude rein darf.",
        Icons.Default.LocationOn,
        AchievementCategory.BEHAVIOR
    )
    val BREMSKLOTZ = Achievement(
        "bremsklotz",
        "Der Fahrstuhl-Bremsklotz",
        "Meiste Minuspunkte in einer Runde",
        "Er hat den Notstopp-Knopf gefunden und ausgiebig getestet.",
        Icons.Default.Stop,
        AchievementCategory.BEHAVIOR
    )


    // 5. Strategie & Chaos
    val SABOTEUR = Achievement(
        "saboteur",
        "Saboteur im Maschinenraum",
        "Gewonnen trotz schlechtester Quote",
        "Niemand weiß wie, aber dieser kaputte Lift ist zuerst oben angekommen.",
        Icons.Default.Build,
        AchievementCategory.STRATEGY
    )
    val ARCHITEKT = Achievement(
        "architekt",
        "Der Architekt",
        "Wenigste Stiche insgesamt, aber gewonnen",
        "Effizienz ist alles. Mit minimalem Aufwand ins Penthouse.",
        Icons.Default.Architecture,
        AchievementCategory.STRATEGY
    )
    val GLUECKSRITTER = Achievement(
        "gluecksritter",
        "Glücksritter im Goldrausch",
        "3 Runden hintereinander exakt getroffen (hoch)",
        "Er liest die Karten wie der Concierge die Morgenzeitung.",
        Icons.Default.Stars,
        AchievementCategory.STRATEGY
    )

    // Intermediate / Round Achievements
    val DOPPELDECKER = Achievement(
        "doppeldecker",
        "Doppeldecker",
        "Zwei Runden hintereinander exakt getroffen",
        "Zwei Etagen ohne Ruckeln. Läuft!",
        Icons.Default.Layers,
        AchievementCategory.SPECIAL
    )
    
    val NOTFALL_HALT = Achievement(
        "notfall_halt",
        "Notfall-Halt",
        "Zwei Runden hintereinander 0 Stiche gemacht",
        "Stillstand auf ganzer Linie. Aber gewollt?",
        Icons.Default.Report,
        AchievementCategory.SPECIAL
    )

    val ALL_ACHIEVEMENTS = listOf(
        SCHWEIZER_LIFTFUEHRER, ERFAHRENER_CONCIERGE, WACKELKONTAKT, TECHNISCHER_DEFEKT,
        PENTHOUSE_STUERMER, KELLERKIND, VORSICHTIGER_PORTIER, FAHRSTUHL_ROWDY,
        FREIER_FALL, UEBERBELEGUNG, GESCHLOSSENE_TUER, STECKENGEBLIEBEN,
        EWIGER_ZWEITE, EXPRESS_LIFT, BODENSTATION, BREMSKLOTZ,
        SABOTEUR, ARCHITEKT, GLUECKSRITTER,
        DOPPELDECKER, NOTFALL_HALT
    )
}
