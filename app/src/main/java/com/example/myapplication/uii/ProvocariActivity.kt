package com.example.myapplication.uii

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.GameMode
import com.example.myapplication.data.UserWordsRepository
import com.example.myapplication.data.WordRepository
import com.example.myapplication.game.ProvocariGame
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.AppTypography
import kotlinx.coroutines.launch
import com.example.myapplication.ui.theme.ButtonS
import com.example.myapplication.ui.theme.SecondaryButton

enum class ProvocarileUiState {
    MENU,
    GAME,
    VIEW_CHALLENGES,
    ADD_CHALLENGE
}

enum class ChallengeSource {
    DEFAULT,
    PERSONALIZAT
}

class ProvocariActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvocariScreen()
        }
    }
}

@Composable
fun ProvocariScreen() {
    val context = LocalContext.current
    val game = remember { ProvocariGame() }
    val wordsRepo = remember { UserWordsRepository() }
    val scope = rememberCoroutineScope()
    var screen by remember { mutableStateOf(ProvocarileUiState.MENU) }
    var currentChallenge by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var challengeSource by remember { mutableStateOf(ChallengeSource.PERSONALIZAT) }
    var cachedChallenges by remember { mutableStateOf<List<String>>(emptyList()) }
    var userChallenges by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var newChallenge by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(colors = AppColors.backgroundGradient)),
        contentAlignment = Alignment.Center)
    {
        Column(modifier = Modifier.width(340.dp)
            .background(AppColors.White, RoundedCornerShape(16.dp))
            .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp))
        {

            when (screen) {
                ProvocarileUiState.MENU -> {
                    Text(
                        text = "Provocări",
                        style = AppTypography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )


                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
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
                                    text = "Sursa provocărilor",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.Purple
                                )
                                Text(
                                    text = if (challengeSource == ChallengeSource.DEFAULT)
                                        "Doar provocări default"
                                    else
                                        "Default + personalizate",
                                    fontSize = 14.sp,
                                    color = AppColors.Purple.copy(alpha = 0.7f)
                                )
                            }

                            Switch(
                                checked = challengeSource == ChallengeSource.PERSONALIZAT,
                                onCheckedChange = { isChecked ->
                                    challengeSource = if (isChecked)
                                        ChallengeSource.PERSONALIZAT
                                    else
                                        ChallengeSource.DEFAULT
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AppColors.Purple,
                                    checkedTrackColor = AppColors.Purple.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    ButtonS(text = "Start", isLoading = isLoading) {
                        isLoading = true
                        scope.launch {
                            try {
                                if (challengeSource == ChallengeSource.DEFAULT) {
                                    cachedChallenges =  WordRepository.provocari
                                } else {
                                    val userChallengesList = wordsRepo.getWords(GameMode.PROVOCARI)
                                    val userList = userChallengesList.map { it.second }
                                    cachedChallenges = (WordRepository.provocari + userList).distinct()
                                }

                                game.loadFromCache(cachedChallenges)
                                currentChallenge = game.getNextChallenge()

                                screen = ProvocarileUiState.GAME

                            } catch (e: Exception) {
                                Toast.makeText(context, "Eroare la încărcarea provocărilor", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }

                    ButtonS("Vezi provocările") {
                        screen = ProvocarileUiState.VIEW_CHALLENGES
                    }

                    ButtonS("Adaugă o provocare nouă") {
                        newChallenge = ""
                        screen = ProvocarileUiState.ADD_CHALLENGE
                    }

                    SecondaryButton("Înapoi") {
                        (context as? ComponentActivity)?.finish()
                    }
                }

                ProvocarileUiState.GAME -> {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.VeryLightPurple
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentChallenge,
                                style = AppTypography.titleLarge.copy(fontSize = 20.sp),
                                textAlign = TextAlign.Center,
                                color = AppColors.Purple
                            )
                        }
                    }

                    ButtonS("Următoarea provocare") {
                        currentChallenge = game.getNextChallenge()
                    }

                    SecondaryButton("Înapoi") {
                        screen = ProvocarileUiState.MENU
                    }
                }

                ProvocarileUiState.VIEW_CHALLENGES -> {
                    LaunchedEffect(Unit) {
                        isLoading = true
                        try {
                            userChallenges = wordsRepo.getWords(GameMode.PROVOCARI)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Eroare la încărcarea provocărilor", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }

                    Text(
                        text = "Provocările mele",
                        style = AppTypography.titleLarge.copy(fontSize = 22.sp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.Purple,
                            modifier = Modifier.padding(32.dp)
                        )
                    } else {
                        if (userChallenges.isEmpty()) {
                            Text(
                                text = "Nu ai provocări personalizate.\nAdaugă una nouă!",
                                style = AppTypography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp),
                                color = AppColors.Purple.copy(alpha = 0.6f)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp)
                                    .padding(vertical = 8.dp)
                            ) {
                                items(userChallenges) { (id, challenge) ->
                                    ChallengeItem(
                                        challenge = challenge,
                                        onDelete = {
                                            userChallenges = userChallenges.filter { it.first != id }
                                            scope.launch {
                                                wordsRepo.deleteWord(GameMode.PROVOCARI, id)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SecondaryButton("Înapoi") {
                        screen = ProvocarileUiState.MENU
                    }
                }

                ProvocarileUiState.ADD_CHALLENGE -> {
                    Text(
                        text = "Adaugă o provocare nouă",
                        style = AppTypography.titleLarge.copy(fontSize = 22.sp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = newChallenge,
                        onValueChange = { newChallenge = it },
                        label = { Text("Provocare") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )

                    ButtonS(text = "Salvează", enabled = newChallenge.isNotBlank())
                    {
                        val challengeToAdd = newChallenge.trim()
                        newChallenge = ""
                        showSuccessDialog = true

                        scope.launch {
                            try {
                                val success = wordsRepo.addWord(mode = GameMode.PROVOCARI, word = challengeToAdd, hint = "")
                            } catch (e: Exception) {
                                Toast.makeText(context, "Provocarea nu a putut fi adăugată", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    SecondaryButton("Înapoi") {
                        screen = ProvocarileUiState.MENU
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
                Text("Provocare adăugată cu succes!")
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
fun ChallengeItem(challenge: String, onDelete: () -> Unit)
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
                text = challenge,
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