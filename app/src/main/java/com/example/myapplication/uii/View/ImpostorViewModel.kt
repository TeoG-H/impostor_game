package com.example.myapplication.uii.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.GameMode
import com.example.myapplication.data.UserWordsRepository
import com.example.myapplication.data.WordRepository
import com.example.myapplication.game.ImpostorGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ImpostorState {
    MODE_SELECT,
    INPUT,
    GAME,
    VIEW_WORDS,
    ADD_WORD
}

enum class WordSource {
    DEFAULT,
    PERSONALIZAT
}

data class ImpostorUiData(
    val screen: ImpostorState = ImpostorState.MODE_SELECT,
    val selectedMode: GameMode? = null,
    val inputText: String = "",
    val players: Int = 0,
    val isLoading: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val wordSource: WordSource = WordSource.PERSONALIZAT,
    val userWords: List<Pair<String, String>> = emptyList(),
    val userWordsWithHint: List<Triple<String, String, String>> = emptyList(),
    val cachedWords: List<String> = emptyList(),
    val cachedPairs: List<Pair<String, String>> = emptyList(),
    val resetKey: Int = 0
)

class ImpostorViewModel(
    //constructor, clasa are nevoie de cele 2 obiecte
    private val wordsRepo: UserWordsRepository = UserWordsRepository(),
    private val game: ImpostorGame = ImpostorGame()
) : ViewModel() {

    // cu "_" inseamna intern, modificabil, si fara e extern read-only
    // am o lista cu toti parametrii si StateFLow notifica cand apare o schimbare

    private val _uiState = MutableStateFlow(ImpostorUiData())
    val uiState: StateFlow<ImpostorUiData> = _uiState.asStateFlow()   // asStateFlow pastreaza aceeasi valoare dar o expune ca read-only

    fun selectMode(mode: GameMode) {
        //obiectul este mutabil (Adica _uiState poate primi o alta referinta) dar datele sunt iumutabile
        // fac copy pt detecteaza schimbare doar cand e un obiect nou nu cand e o valoarea noua
        _uiState.value = _uiState.value.copy(
            selectedMode = mode,
            screen = ImpostorState.INPUT
        )
    }

    fun updateInput(text: String) {
        if (text.all { it.isDigit() } && text.length <= 2) {
            _uiState.value = _uiState.value.copy(inputText = text)
        }
    }

    fun changeWordSource(source: WordSource) {
        _uiState.value = _uiState.value.copy(wordSource = source)
    }

    fun startGame() {
        val mode = _uiState.value.selectedMode ?: return
        val players = _uiState.value.inputText.toIntOrNull() ?: return
        if (players !in 3..10) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                when (mode) {
                    GameMode.FARA_AJUTOR, GameMode.NUMAR -> {
                        val defaultList = WordRepository.getWords(mode)

                        val finalList =
                            if (_uiState.value.wordSource == WordSource.DEFAULT || mode == GameMode.NUMAR) {
                                defaultList
                            } else {
                                val userList = wordsRepo.getWords(mode).map { it.second }
                                (defaultList + userList).distinct()
                            }

                        game.start(players, mode, finalList, emptyList())

                        _uiState.value = _uiState.value.copy(
                            cachedWords = finalList,
                            players = players,
                            screen = ImpostorState.GAME,
                            resetKey = _uiState.value.resetKey + 1
                        )
                    }

                    GameMode.CU_AJUTOR, GameMode.CUVANT_SIMILAR -> {
                        val defaultPairs = WordRepository.getPair(mode)

                        val finalPairs =
                            if (_uiState.value.wordSource == WordSource.DEFAULT) {
                                defaultPairs
                            } else {
                                val userPairs = wordsRepo.getWordsWithHint(mode)
                                    .map { Pair(it.second, it.third) }
                                (defaultPairs + userPairs).distinct()
                            }

                        game.start(players, mode, emptyList(), finalPairs)

                        _uiState.value = _uiState.value.copy(
                            cachedPairs = finalPairs,
                            players = players,
                            screen = ImpostorState.GAME,
                            resetKey = _uiState.value.resetKey + 1
                        )
                    }

                    else -> {}
                }

            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun resetGame() {
        val state = _uiState.value
        val mode = state.selectedMode ?: return

        game.start(
            state.players,
            mode,
            state.cachedWords,
            state.cachedPairs
        )

        _uiState.value =
            state.copy(resetKey = state.resetKey + 1)
    }

    fun getTextForPlayer(index: Int): String {
        return game.getTextForPlayer(index)
    }

    fun goTo(screen: ImpostorState) {
        _uiState.value = _uiState.value.copy(screen = screen)
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }

    fun showDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = true)
    }
}
