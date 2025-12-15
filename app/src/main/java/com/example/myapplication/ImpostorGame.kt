package com.example.myapplication

import kotlin.random.Random

class ImpostorGame {

    private lateinit var pair: Pair<String, String>
    private var impostorIndex = 0
    private lateinit var mode: GameMode

    fun start(players: Int, mode: GameMode) {
        this.mode = mode
        impostorIndex = Random.nextInt(1, players + 1)
        pair = WordRepository.getWords(mode).random()
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
            }
        } else {
            pair.first
        }
    }
}
