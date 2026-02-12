package com.example.myapplication.uii

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.game.ImpostorGame
import com.example.myapplication.data.GameMode
import com.example.myapplication.data.UserWordsRepository
import com.example.myapplication.data.WordRepository
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.AppTypography
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.theme.ButtonS
import com.example.myapplication.ui.theme.SecondaryButton
import com.example.myapplication.ui.theme.ModeButton


enum class ImpostorUiState {
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

class ImpostorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImpostorScreen()
        }
    }
}

@Composable
fun ImpostorScreen() {

    val context = LocalContext.current
    val game = remember { ImpostorGame() }
    val wordsRepo = remember { UserWordsRepository() }
    val scope = rememberCoroutineScope()

    var screen by remember { mutableStateOf(ImpostorUiState.MODE_SELECT) }
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var inputText by remember { mutableStateOf("") }
    var players by remember { mutableIntStateOf(0) }
    var resetKey by remember { mutableIntStateOf(0) }
    var userWords by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var userWordsWithHint by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }

    var newWord by remember { mutableStateOf("") }
    var newHint by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var refresh by remember { mutableIntStateOf(0) }
    var cachedWords by remember { mutableStateOf<List<String>>(emptyList()) }
    var cachedPairs by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var wordSource by remember { mutableStateOf(WordSource.PERSONALIZAT) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = AppColors.backgroundGradient)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .width(340.dp)
                .background(AppColors.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {

            if (screen == ImpostorUiState.MODE_SELECT) {
                Text(
                    text = "Impostor",
                    style = AppTypography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            when (screen) {

                ImpostorUiState.MODE_SELECT -> {
                    ModeButton("Fără ajutor") {
                        selectedMode = GameMode.FARA_AJUTOR; screen = ImpostorUiState.INPUT
                    }

                    ModeButton("Cu ajutor") {
                        selectedMode = GameMode.CU_AJUTOR; screen = ImpostorUiState.INPUT
                    }

                    ModeButton("Cuvânt similar") {
                        selectedMode = GameMode.CUVANT_SIMILAR; screen = ImpostorUiState.INPUT
                    }

                    ModeButton("Număr") {
                        selectedMode = GameMode.NUMAR; screen = ImpostorUiState.INPUT
                    }

                    SecondaryButton("Înapoi") {
                        (context as? ComponentActivity)?.finish()
                    }
                }


                ImpostorUiState.INPUT -> {
                    if (selectedMode != GameMode.NUMAR) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AppColors.VeryLightPurple
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Sursa cuvintelor",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.Purple
                                    )
                                    Text(
                                        text = if (wordSource == WordSource.DEFAULT)
                                            "Doar cuvinte default"
                                        else
                                            "Default + personalizate",
                                        fontSize = 14.sp,
                                        color = AppColors.Purple.copy(alpha = 0.7f)
                                    )
                                }

                                Switch(
                                    checked = wordSource == WordSource.PERSONALIZAT,
                                    onCheckedChange = { isChecked ->
                                        wordSource = if (isChecked) WordSource.PERSONALIZAT else WordSource.DEFAULT
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AppColors.Purple,
                                        checkedTrackColor = AppColors.Purple.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { new ->
                            if (new.all { it.isDigit() } && new.length <= 2) {
                                inputText = new
                            }
                        },
                        label = { Text("Nr jucători (3-10)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )


                    val isInputValid = inputText.toIntOrNull()?.let { it in 3..10 } == true

                    ButtonS("Start", enabled = isInputValid, isLoading = isLoading) {
                        val value = inputText.toIntOrNull() ?: return@ButtonS
                        if (value !in 3..10) return@ButtonS

                        players = value
                        isLoading = true

                        scope.launch {
                            try {
                                when (selectedMode) {

                                    GameMode.FARA_AJUTOR, GameMode.NUMAR -> {
                                        val defaultList = WordRepository.getWords(selectedMode!!)

                                        cachedWords = if (selectedMode == GameMode.NUMAR) {
                                            defaultList
                                        } else {
                                            if (wordSource == WordSource.DEFAULT) {
                                                defaultList
                                            } else {
                                                val userWordsList = wordsRepo.getWords(selectedMode!!)
                                                val userList = userWordsList.map { it.second }
                                                (defaultList + userList).distinct()
                                            }
                                        }
                                    }

                                    GameMode.CU_AJUTOR, GameMode.CUVANT_SIMILAR -> {
                                        val defaultPairs = WordRepository.getPair(selectedMode!!)

                                        cachedPairs = if (wordSource == WordSource.DEFAULT) {
                                            defaultPairs
                                        } else {
                                            val userPairsList = wordsRepo.getWordsWithHint(selectedMode!!)
                                            val userList = userPairsList.map { Pair(it.second, it.third) }
                                            (defaultPairs + userList).distinct()
                                        }
                                    }

                                    else -> {}
                                }

                                game.startFromCache(players, selectedMode!!, cachedWords, cachedPairs)
                                resetKey++
                                screen = ImpostorUiState.GAME

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Eroare la încărcarea jocului",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }


                    if (selectedMode != GameMode.NUMAR) {
                        ButtonS("Vezi lista de cuvinte") {
                            screen = ImpostorUiState.VIEW_WORDS
                        }

                        ButtonS("Adaugă cuvânt nou") {
                            newWord = ""
                            newHint = ""
                            screen = ImpostorUiState.ADD_WORD
                        }

                    }

                    SecondaryButton("Înapoi") {
                        screen = ImpostorUiState.MODE_SELECT
                    }

                }


                ImpostorUiState.VIEW_WORDS -> {

                    LaunchedEffect(Unit) {
                        isLoading = true
                        try {
                            if (selectedMode == GameMode.CU_AJUTOR || selectedMode == GameMode.CUVANT_SIMILAR) {
                                userWordsWithHint = wordsRepo.getWordsWithHint(selectedMode!!)
                            } else {
                                userWords = wordsRepo.getWords(selectedMode!!)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Eroare la încărcarea cuvintelor",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }

                    Text(
                        text = "Cuvintele mele",
                        style = AppTypography.titleLarge.copy(fontSize = 22.sp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.Purple,
                            modifier = Modifier.padding(32.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .padding(vertical = 8.dp)
                        ) {
                            if (selectedMode == GameMode.CU_AJUTOR || selectedMode == GameMode.CUVANT_SIMILAR) {
                                items(userWordsWithHint) { (id, word, hint) ->
                                    WordItemWithHint(
                                        word = word,
                                        hint = hint,
                                        onDelete = {
                                            userWordsWithHint =
                                                userWordsWithHint.filter { it.first != id }
                                            scope.launch {
                                                wordsRepo.deleteWord(selectedMode!!, id)
                                            }
                                        }
                                    )
                                }
                            } else {
                                items(userWords) { (id, word) ->
                                    WordItem(
                                        word = word,
                                        onDelete = {
                                            userWords = userWords.filter { it.first != id }
                                            scope.launch {
                                                wordsRepo.deleteWord(selectedMode!!, id)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }


                    SecondaryButton("Înapoi") { inputText = ""; screen = ImpostorUiState.INPUT }

                }


                ImpostorUiState.ADD_WORD -> {

                    Text(
                        text = "Adaugă cuvânt nou",
                        style = AppTypography.titleLarge.copy(fontSize = 22.sp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = newWord,
                        onValueChange = { newWord = it },
                        label = { Text("Cuvânt") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (selectedMode == GameMode.CU_AJUTOR || selectedMode == GameMode.CUVANT_SIMILAR) {
                        OutlinedTextField(
                            value = newHint,
                            onValueChange = { newHint = it },
                            label = {
                                Text(
                                    if (selectedMode == GameMode.CU_AJUTOR)
                                        "Hint"
                                    else
                                        "Cuvânt similar"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                    }

                    ButtonS(
                        text = "Salvează",
                        enabled = newWord.isNotBlank()
                    ) {

                        val wordToAdd = newWord.trim()
                        val hintToAdd = newHint.trim()

                        newWord = ""
                        newHint = ""
                        showSuccessDialog = true

                        scope.launch {
                            try {

                                val success = wordsRepo.addWord(mode = selectedMode!!, word = wordToAdd, hint = hintToAdd)

                            } catch (e: Exception) {
                                Toast.makeText(context, "Cuvântul nu a putut fi adăugat", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                    SecondaryButton("Înapoi") {
                        inputText = ""; screen = ImpostorUiState.INPUT
                    }
                }


                ImpostorUiState.GAME -> {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .heightIn(max = 500.dp)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(players) { index ->
                            ImpostorCell(resetKey = resetKey) {
                                game.getTextForPlayer(index + 1)
                            }
                        }
                    }


                    ButtonS("Reset") {
                        game.startFromCache(players, selectedMode!!, cachedWords, cachedPairs)
                        resetKey++
                    }


                    Spacer(Modifier.height(8.dp))

                    SecondaryButton("Înapoi") {
                        inputText = ""
                        screen = ImpostorUiState.MODE_SELECT
                    }
                }
            }
        }
    }


    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Text(
                    text = "✓ Succes!",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Purple
                )
            },
            text = {
                Text("Cuvânt adăugat cu succes!")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Purple
                    ),
                    onClick = { showSuccessDialog = false }
                ) {
                    Text("OK")
                }
            },
            containerColor = AppColors.White
        )
    }
}


@Composable
fun WordItem(word: String, onDelete: () -> Unit)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.VeryLightPurple
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Șterge",
                    tint = AppColors.Purple
                )
            }
        }
    }
}

@Composable
fun WordItemWithHint(word: String, hint: String, onDelete: () -> Unit)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.VeryLightPurple
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (hint.isNotBlank()) {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        color = AppColors.Gray
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Șterge",
                    tint = AppColors.Purple
                )
            }
        }
    }
}

@Composable
fun ImpostorCell(resetKey: Int, getText: () -> String) {
    var state by remember(resetKey) { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(120.dp)
            .background(
                color = if (state == 0) AppColors.Purple else AppColors.DarkPurple,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = state < 2) {
                state++
            },
        contentAlignment = Alignment.Center
    ) {
        if (state == 1) {
            Text(
                text = getText(),
                color = AppColors.White,
                style = AppTypography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
