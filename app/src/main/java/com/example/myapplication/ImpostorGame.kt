package com.example.myapplication

import kotlin.random.Random

class ImpostorGame {

    private  var pair = Pair("","S")
    private  var text = ""
    private var impostorIndex = 0
    private lateinit var mode: GameMode

    fun start(players: Int, mode: GameMode) {
        this.mode = mode
        impostorIndex = Random.nextInt(1, players + 1)
        if(mode== GameMode.FARA_AJUTOR || mode== GameMode.NUMAR)
        {
            text= WordRepository.getWords(mode).random()
        }
        else {
            pair = WordRepository.getPair(mode).random()
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
            }
        } else {
            when (mode) {
                GameMode.FARA_AJUTOR,
                GameMode.NUMAR ->
                    text

                GameMode.CU_AJUTOR,
                GameMode.CUVANT_SIMILAR ->
                    pair.first
            }
        }
    }
}
