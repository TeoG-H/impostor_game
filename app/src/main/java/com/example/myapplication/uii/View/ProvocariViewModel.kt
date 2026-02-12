package com.example.myapplication.uii.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.GameMode
import com.example.myapplication.data.UserWordsRepository
import com.example.myapplication.data.WordRepository
import com.example.myapplication.game.ProvocariGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ChallengeState {
    MENU,
    GAME,
    VIEW_CHALLENGES,
    ADD_CHALLENGE
}

enum class ChallengeSource {
    DEFAULT,
    PERSONALIZAT
}

data class ProvocariUiState(
    val screen: ChallengeState = ChallengeState.MENU,
    val currentChallenge: String = "",
    val isLoading: Boolean = false,
    val challengeSource: ChallengeSource = ChallengeSource.PERSONALIZAT,
    val cachedChallenges: List<String> = emptyList(),
    val userChallenges: List<Pair<String, String>> = emptyList(),
    val showSuccessDialog: Boolean = false
)

class ProvocariViewModel(
    private val wordsRepo: UserWordsRepository = UserWordsRepository(),
    private val game: ProvocariGame = ProvocariGame()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProvocariUiState())
    val uiState: StateFlow<ProvocariUiState> = _uiState.asStateFlow()

    fun changeSource(source: ChallengeSource) {
        _uiState.value = _uiState.value.copy(challengeSource = source)
    }

    fun goTo(screen: ChallengeState) {
        _uiState.value = _uiState.value.copy(screen = screen)
    }

    fun startGame() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val challenges =
                    if (_uiState.value.challengeSource == ChallengeSource.DEFAULT) {
                        WordRepository.provocari
                    } else {
                        val userList = wordsRepo.getWords(GameMode.PROVOCARI)
                            .map { it.second }
                        (WordRepository.provocari + userList).distinct()
                    }

                game.loadFromCache(challenges)
                val first = game.getNextChallenge()

                _uiState.value = _uiState.value.copy(
                    cachedChallenges = challenges,
                    currentChallenge = first,
                    screen = ChallengeState.GAME
                )

            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun nextChallenge() {
        val next = game.getNextChallenge()
        _uiState.value = _uiState.value.copy(currentChallenge = next)
    }

    fun loadUserChallenges() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val list = wordsRepo.getWords(GameMode.PROVOCARI)
                _uiState.value = _uiState.value.copy(userChallenges = list)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun deleteChallenge(id: String) {
        val updated = _uiState.value.userChallenges.filter { it.first != id }
        _uiState.value = _uiState.value.copy(userChallenges = updated)

        viewModelScope.launch {
            wordsRepo.deleteWord(GameMode.PROVOCARI, id)
        }
    }

    fun addChallenge(text: String) {
        _uiState.value = _uiState.value.copy(showSuccessDialog = true)
        viewModelScope.launch {
            wordsRepo.addWord(GameMode.PROVOCARI, text.trim(), "")
        }
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }
}
