package com.example.myapplication.game

import com.example.myapplication.data.GameMode
import kotlin.random.Random

class ImpostorGame {

    private var pair = Pair("", "")
    private var text = ""
    private var impostorIndex = 0
    private lateinit var mode: GameMode

    fun start(players: Int, mode: GameMode, words: List<String>, pairs: List<Pair<String, String>>)
    {
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

    /*
     //private val userWordsRepo = UserWordsRepository()

    suspend fun startAsync(players: Int, mode: GameMode) {
        this.mode = mode
        impostorIndex = Random.Default.nextInt(1, players + 1)

        when (mode) {
            GameMode.FARA_AJUTOR, GameMode.NUMAR -> {
                val userWords = userWordsRepo.getWords(mode)
                text = if (userWords.isNotEmpty()) {
                    userWords.random().second
                } else {
                    WordRepository.getWords(mode).random()
                }
            }
            GameMode.CU_AJUTOR, GameMode.CUVANT_SIMILAR -> {
                val userPairs = userWordsRepo.getWordsWithHint(mode)
                pair = if (userPairs.isNotEmpty()) {
                    val selected = userPairs.random()
                    Pair(selected.second, selected.third) // word și hint
                } else {
                    WordRepository.getPair(mode).random()
                }
            }
            GameMode.PROVOCARI -> {

            }
        }
    }

     */
}