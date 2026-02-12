package com.example.myapplication.game

import com.example.myapplication.data.GameMode
import com.example.myapplication.data.WordRepository
import com.example.myapplication.data.UserWordsRepository
import kotlin.random.Random

class ProvocariGame {

    private var challenges = listOf<String>()
    private var usedChallenges = mutableSetOf<String>()
    private var currentChallenge = ""
    private val userWordsRepo = UserWordsRepository()


    fun loadFromCache(cachedChallenges: List<String>) {
        challenges = cachedChallenges
        usedChallenges.clear()
    }

    fun getNextChallenge(): String {
        if (challenges.isEmpty()) {
            return "Nu există provocări disponibile"
        }

        // daca au fost folosite toate se ia de la inceput
        if (usedChallenges.size >= challenges.size) {
            usedChallenges.clear()
        }

        val availableChallenges = challenges.filter { it !in usedChallenges }

        currentChallenge = if (availableChallenges.isNotEmpty()) {
            availableChallenges.random(Random)
        } else {
            challenges.random(Random)
        }

        usedChallenges.add(currentChallenge)
        return currentChallenge
    }


    /*
    fun getCurrentChallenge(): String = currentChallenge

    suspend fun loadChallenges() {
        val userChallenges = userWordsRepo.getWords(GameMode.PROVOCARI)
        challenges = if (userChallenges.isNotEmpty()) {
            userChallenges.map { it.second }
        } else {
            // Fallback la provocările default
            WordRepository.provocari
        }
        usedChallenges.clear()
    }*/
}