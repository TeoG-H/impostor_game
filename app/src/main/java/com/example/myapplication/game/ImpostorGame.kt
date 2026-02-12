package com.example.myapplication.game

import com.example.myapplication.data.GameMode
import com.example.myapplication.data.WordRepository
import com.example.myapplication.data.UserWordsRepository
import kotlin.random.Random

class ImpostorGame {

    private var pair = Pair("", "")
    private var text = ""
    private var impostorIndex = 0
    private lateinit var mode: GameMode
    private val userWordsRepo = UserWordsRepository()

    /**
     * Versiune ASYNC - folosește-o din coroutines
     */
    suspend fun startAsync(players: Int, mode: GameMode) {
        this.mode = mode
        impostorIndex = Random.Default.nextInt(1, players + 1)

        when (mode) {
            GameMode.FARA_AJUTOR, GameMode.NUMAR -> {
                // Încearcă să ia cuvinte de la user
                val userWords = userWordsRepo.getWords(mode)
                text = if (userWords.isNotEmpty()) {
                    userWords.random().second // al doilea element e cuvântul
                } else {
                    // Fallback la cuvintele default
                    WordRepository.getWords(mode).random()
                }
            }
            GameMode.CU_AJUTOR, GameMode.CUVANT_SIMILAR -> {
                // Încearcă să ia perechi de la user
                val userPairs = userWordsRepo.getWordsWithHint(mode)
                pair = if (userPairs.isNotEmpty()) {
                    val selected = userPairs.random()
                    Pair(selected.second, selected.third) // word și hint
                } else {
                    // Fallback la perechile default
                    WordRepository.getPair(mode).random()
                }
            }
            GameMode.PROVOCARI -> {

            }
        }
    }

    fun startFromCache(
        players: Int,
        mode: GameMode,
        words: List<String>,
        pairs: List<Pair<String, String>>
    ) {
        this.mode = mode
        impostorIndex = Random.Default.nextInt(1, players + 1)

        when (mode) {
            GameMode.FARA_AJUTOR, GameMode.NUMAR -> {
                text = words.random()
            }
            GameMode.CU_AJUTOR, GameMode.CUVANT_SIMILAR -> {
                pair = pairs.random()
            }
            GameMode.PROVOCARI -> {

            }
        }
    }

    fun getTextForPlayer(index: Int): String {
        return if (index == impostorIndex) {
            when (mode) {
                GameMode.FARA_AJUTOR ->
                    "IMPOSTOR"

                GameMode.CU_AJUTOR ->
                    "IMPOSTOR\nHint: ${pair.second}"

                GameMode.CUVANT_SIMILAR ->
                    pair.second

                GameMode.NUMAR ->
                    "IMPOSTOR"
                GameMode.PROVOCARI -> ""
            }
        } else {
            when (mode) {
                GameMode.FARA_AJUTOR,
                GameMode.NUMAR ->
                    text

                GameMode.CU_AJUTOR,
                GameMode.CUVANT_SIMILAR ->
                    pair.first
                GameMode.PROVOCARI -> pair.first
            }
        }
    }
}