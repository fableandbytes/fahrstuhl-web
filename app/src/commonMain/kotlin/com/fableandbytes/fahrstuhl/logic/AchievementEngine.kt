package com.fableandbytes.fahrstuhl.logic

import com.fableandbytes.fahrstuhl.model.Achievement
import com.fableandbytes.fahrstuhl.model.AchievementRegistry
import com.fableandbytes.fahrstuhl.model.Player
import com.fableandbytes.fahrstuhl.model.Round

object AchievementEngine {

    fun calculateEndGameAchievements(players: List<Player>, rounds: List<Round>): List<Player> {
        if (rounds.isEmpty()) return players

        val maxScore = players.maxOf { it.totalScore }
        val winners = players.filter { it.totalScore == maxScore }

        return players.map { player ->
            val earned = mutableListOf<Achievement>()
            val playerRoundsData = rounds.map { round ->
                val pred = round.predictions[player.id] ?: 0
                val res = round.results[player.id] ?: 0
                pred to res
            }
            
            val hitCount = playerRoundsData.count { it.first == it.second }
            val hitRate = hitCount.toDouble() / rounds.size

            // --- 1. Spezial-Leistungen (Priorität 1) ---
            
            // Penthouse-Stürmer
            if (playerRoundsData.any { it.first >= 8 && it.first == it.second }) {
                earned.add(AchievementRegistry.PENTHOUSE_STUERMER)
            }

            // Saboteur im Maschinenraum
            val minHitRate = players.minOf { p -> 
                val pRounds = rounds.map { (it.predictions[p.id] ?: 0) to (it.results[p.id] ?: 0) }
                pRounds.count { it.first == it.second }.toDouble() / rounds.size
            }
            // Saboteur nur, wenn man wirklich gewonnen hat UND die schlechteste Quote hat (Eindeutigkeit bei Gleichstand: beide kriegen es)
            if (winners.any { it.id == player.id } && hitRate <= minHitRate && hitRate < 1.0) {
                earned.add(AchievementRegistry.SABOTEUR)
            }

            // Architekt
            val playerTotalTricks = playerRoundsData.sumOf { it.second }
            val minTotalTricks = players.minOf { p -> 
                rounds.sumOf { it.results[p.id] ?: 0 }
            }
            // Architekt: Gewonnen mit den wenigsten Stichen
            if (winners.any { it.id == player.id } && playerTotalTricks == minTotalTricks) {
                earned.add(AchievementRegistry.ARCHITEKT)
            }

            // Glücksritter im Goldrausch
            var consecutiveHits = 0
            var hasGoldrausch = false
            for (data in playerRoundsData) {
                if (data.first == data.second && data.first >= 3) {
                    consecutiveHits++
                    if (consecutiveHits >= 3) hasGoldrausch = true
                } else {
                    consecutiveHits = 0
                }
            }
            if (hasGoldrausch) earned.add(AchievementRegistry.GLUECKSRITTER)

            // Im freien Fall
            if (playerRoundsData.any { it.first >= 5 && it.second == 0 }) {
                earned.add(AchievementRegistry.FREIER_FALL)
            }

            // Überbelegung
            if (playerRoundsData.any { it.second >= it.first + 3 }) {
                earned.add(AchievementRegistry.UEBERBELEGUNG)
            }

            // Steckengeblieben
            val wasWinnerBeforeLast = if (rounds.size > 1) {
                val scoresBeforeLast = rounds[rounds.size - 2].cumulativeScores
                val maxScoreBefore = scoresBeforeLast.values.maxOrNull() ?: 0
                scoresBeforeLast[player.id] == maxScoreBefore
            } else false
            if (wasWinnerBeforeLast && !winners.any { it.id == player.id }) {
                earned.add(AchievementRegistry.STECKENGEBLIEBEN)
            }

            // --- 2. Verhaltensweisen (Priorität 2) ---
            if (earned.isEmpty()) {
                // Ewiger Zweite
                var secondPlaceCount = 0
                var everFirst = false
                rounds.forEach { round ->
                    val sorted = round.cumulativeScores.values.distinct().sortedDescending()
                    val pScore = round.cumulativeScores[player.id] ?: 0
                    if (sorted.indexOf(pScore) == 0) everFirst = true
                    if (sorted.size > 1 && sorted.indexOf(pScore) == 1) secondPlaceCount++
                }
                if (secondPlaceCount > rounds.size / 2 && !everFirst) {
                    earned.add(AchievementRegistry.EWIGER_ZWEITE)
                }

                // Kellerkind
                val zeroAnsagenRate = playerRoundsData.count { it.first == 0 }.toDouble() / rounds.size
                if (zeroAnsagenRate > 0.7) {
                    earned.add(AchievementRegistry.KELLERKIND)
                }

                // Der vorsichtige Portier
                val zeroZeroRounds = playerRoundsData.count { it.first == 0 && it.second == 0 }
                if (zeroZeroRounds > rounds.size / 2) {
                    earned.add(AchievementRegistry.VORSICHTIGER_PORTIER)
                }

                // Bodenstation
                val alwaysLast = rounds.all { round ->
                    val minScore = round.cumulativeScores.values.minOrNull() ?: 0
                    round.cumulativeScores[player.id] == minScore
                }
                if (alwaysLast) earned.add(AchievementRegistry.BODENSTATION)
            }

            // --- 3. Fallback: Präzisions-Klasse (Priorität 3) ---
            if (earned.isEmpty()) {
                when {
                    hitRate == 1.0 -> earned.add(AchievementRegistry.SCHWEIZER_LIFTFUEHRER)
                    hitRate > 0.8 -> earned.add(AchievementRegistry.ERFAHRENER_CONCIERGE)
                    hitRate >= 0.5 -> earned.add(AchievementRegistry.WACKELKONTAKT)
                    else -> earned.add(AchievementRegistry.TECHNISCHER_DEFEKT)
                }
            }
            

            player.copy(achievements = earned.distinct())
        }
    }

    fun getIntermediateAchievements(players: List<Player>, rounds: List<Round>): Map<Int, List<Achievement>> {
        if (rounds.isEmpty()) return emptyMap()
        val result = mutableMapOf<Int, List<Achievement>>()

        players.forEach { player ->
            val earned = mutableListOf<Achievement>()
            val playerRounds = rounds.filter { it.predictions.containsKey(player.id) }
            
            if (playerRounds.size >= 2) {
                val lastTwo = playerRounds.takeLast(2)
                val r1 = lastTwo[0]
                val r2 = lastTwo[1]
                
                // Doppeldecker
                if (r1.predictions[player.id] == r1.results[player.id] && 
                    r2.predictions[player.id] == r2.results[player.id]) {
                    earned.add(AchievementRegistry.DOPPELDECKER)
                }
                
                // Notfall-Halt
                if (r1.results[player.id] == 0 && r2.results[player.id] == 0) {
                    earned.add(AchievementRegistry.NOTFALL_HALT)
                }
            }
            
            if (earned.isNotEmpty()) {
                result[player.id] = earned
            }
        }
        
        return result
    }
}
