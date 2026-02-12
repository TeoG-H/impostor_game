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
import com.example.myapplication.data.GameMode
import com.example.myapplication.data.UserWordsRepository
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.AppTypography
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.theme.ButtonS
import com.example.myapplication.ui.theme.SecondaryButton
import com.example.myapplication.ui.theme.ModeButton
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.uii.View.ImpostorViewModel
import com.example.myapplication.uii.View.ImpostorState
import com.example.myapplication.uii.View.WordSource


class ImpostorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImpostorScreen()
        }
    }
}

@Composable
fun ImpostorScreen(viewModel: ImpostorViewModel = viewModel()) {
    val context = LocalContext.current
    val wordsRepo = remember { UserWordsRepository() }
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()

    var userWords by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var userWordsWithHint by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }

    var newWord by remember { mutableStateOf("") }
    var newHint by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(colors = AppColors.backgroundGradient)),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.width(340.dp)
                .background(AppColors.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (uiState.screen == ImpostorState.MODE_SELECT) {
                Text(text = "Impostor",
                    style = AppTypography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp))
            }

            when (uiState.screen) {

                ImpostorState.MODE_SELECT -> {
                    ModeButton("Fără ajutor") { viewModel.selectMode(GameMode.FARA_AJUTOR) }

                    ModeButton("Cu ajutor") { viewModel.selectMode(GameMode.CU_AJUTOR) }

                    ModeButton("Cuvânt similar") { viewModel.selectMode(GameMode.CUVANT_SIMILAR) }

                    ModeButton("Număr") { viewModel.selectMode(GameMode.NUMAR) }

                    SecondaryButton("Înapoi") { (context as? ComponentActivity)?.finish() }
                }


                ImpostorState.INPUT -> {
                    if (uiState.selectedMode != GameMode.NUMAR) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.VeryLightPurple)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                                        text = if (uiState.wordSource == WordSource.DEFAULT)
                                            "Doar cuvinte default"
                                        else
                                            "Default + personalizate",
                                        fontSize = 14.sp,
                                        color = AppColors.Purple.copy(alpha = 0.7f)
                                    )
                                }

                                Switch(
                                    checked = uiState.wordSource == WordSource.PERSONALIZAT,
                                    onCheckedChange = { isChecked ->
                                        viewModel.changeWordSource(
                                            if (isChecked) WordSource.PERSONALIZAT else WordSource.DEFAULT
                                        )
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
                        value = uiState.inputText,
                        onValueChange = { viewModel.updateInput(it) },
                        label = { Text("Nr jucători (3-10)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    val isInputValid = uiState.inputText.toIntOrNull()?.let { it in 3..10 } == true

                    ButtonS("Start", enabled = isInputValid, isLoading = uiState.isLoading) {
                        viewModel.startGame()
                    }

                    if (uiState.selectedMode != GameMode.NUMAR) {
                        ButtonS("Vezi lista de cuvinte") {
                            viewModel.goTo(ImpostorState.VIEW_WORDS)
                        }

                        ButtonS("Adaugă cuvânt nou") {
                            newWord = ""
                            newHint = ""
                            viewModel.goTo(ImpostorState.ADD_WORD)
                        }
                    }

                    SecondaryButton("Înapoi") {
                        viewModel.goTo(ImpostorState.MODE_SELECT)
                    }
                }


                ImpostorState.VIEW_WORDS -> {

                    LaunchedEffect(Unit) {
                        isLoading = true
                        try {
                            if (uiState.selectedMode == GameMode.CU_AJUTOR || uiState.selectedMode == GameMode.CUVANT_SIMILAR) {
                                userWordsWithHint = wordsRepo.getWordsWithHint(uiState.selectedMode!!)
                            } else {
                                userWords = wordsRepo.getWords(uiState.selectedMode!!)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Eroare la incarcarea cuvintelor", Toast.LENGTH_SHORT).show()
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
                            if (uiState.selectedMode == GameMode.CU_AJUTOR || uiState.selectedMode == GameMode.CUVANT_SIMILAR) {
                                items(userWordsWithHint) { (id, word, hint) ->
                                    WordItemWithHint(
                                        word = word,
                                        hint = hint,
                                        onDelete = {
                                            userWordsWithHint = userWordsWithHint.filter { it.first != id }
                                            scope.launch {
                                                wordsRepo.deleteWord(uiState.selectedMode!!, id)
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
                                                wordsRepo.deleteWord(uiState.selectedMode!!, id)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SecondaryButton("Înapoi") {
                        viewModel.goTo(ImpostorState.INPUT)
                    }
                }


                ImpostorState.ADD_WORD -> {

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

                    if (uiState.selectedMode == GameMode.CU_AJUTOR || uiState.selectedMode == GameMode.CUVANT_SIMILAR) {
                        OutlinedTextField(
                            value = newHint,
                            onValueChange = { newHint = it },
                            label = {
                                Text(
                                    if (uiState.selectedMode == GameMode.CU_AJUTOR)
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
                        viewModel.showDialog()

                        scope.launch {
                            try {
                                wordsRepo.addWord(
                                    mode = uiState.selectedMode!!,
                                    word = wordToAdd,
                                    hint = hintToAdd
                                )
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cuvantul nu a putut fi adaugat", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    SecondaryButton("Înapoi") {
                        viewModel.goTo(ImpostorState.INPUT)
                    }
                }


                ImpostorState.GAME -> {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.heightIn(max = 500.dp).padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(uiState.players) { index ->
                            ImpostorCell(resetKey = uiState.resetKey) {
                                viewModel.getTextForPlayer(index + 1)
                            }
                        }
                    }

                    ButtonS("Reset") {
                        viewModel.resetGame()
                    }

                    Spacer(Modifier.height(8.dp))

                    SecondaryButton("Înapoi") {
                        viewModel.goTo(ImpostorState.MODE_SELECT)
                    }
                }
            }
        }
    }

    if (uiState.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
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
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple),
                    onClick = { viewModel.dismissDialog() }
                ) {
                    Text("OK")
                }
            },
            containerColor = AppColors.White
        )
    }
}


@Composable
fun WordItem(word: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.VeryLightPurple
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
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
fun WordItemWithHint(word: String, hint: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.VeryLightPurple)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
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