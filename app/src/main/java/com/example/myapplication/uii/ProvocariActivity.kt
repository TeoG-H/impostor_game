package com.example.myapplication.uii

import android.os.Bundle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.AppTypography
import com.example.myapplication.ui.theme.ButtonS
import com.example.myapplication.ui.theme.SecondaryButton
import com.example.myapplication.uii.View.*

class ProvocariActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvocariScreen()
        }
    }
}

@Composable
fun ProvocariScreen(
    viewModel: ProvocariViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var newChallenge by remember { mutableStateOf("") }

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

            when (uiState.screen) {
                ChallengeState.MENU -> {

                    Text(
                        text = "Provocări",
                        style = AppTypography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    text =
                                        if (uiState.challengeSource == ChallengeSource.DEFAULT)
                                            "Doar provocări default"
                                        else
                                            "Default + personalizate",
                                    fontSize = 14.sp,
                                    color = AppColors.Purple.copy(alpha = 0.7f)
                                )
                            }

                            Switch(
                                checked = uiState.challengeSource == ChallengeSource.PERSONALIZAT,
                                onCheckedChange = {
                                    viewModel.changeSource(
                                        if (it)
                                            ChallengeSource.PERSONALIZAT
                                        else
                                            ChallengeSource.DEFAULT
                                    )
                                }
                            )
                        }
                    }

                    ButtonS(
                        text = "Start",
                        isLoading = uiState.isLoading
                    ) {
                        viewModel.startGame()
                    }

                    ButtonS("Vezi provocările") {
                        viewModel.goTo(ChallengeState.VIEW_CHALLENGES)
                    }

                    ButtonS("Adaugă o provocare nouă") {
                        newChallenge = ""
                        viewModel.goTo(ChallengeState.ADD_CHALLENGE)
                    }

                    SecondaryButton("Înapoi") {
                        (context as? ComponentActivity)?.finish()
                    }
                }

                ChallengeState.GAME -> {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.VeryLightPurple
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.currentChallenge,
                                style = AppTypography.titleLarge.copy(fontSize = 20.sp),
                                textAlign = TextAlign.Center,
                                color = AppColors.Purple
                            )
                        }
                    }

                    ButtonS("Următoarea provocare") {
                        viewModel.nextChallenge()
                    }

                    SecondaryButton("Înapoi") {
                        viewModel.goTo(ChallengeState.MENU)
                    }
                }

                ChallengeState.VIEW_CHALLENGES -> {

                    LaunchedEffect(Unit) {
                        viewModel.loadUserChallenges()
                    }

                    Text(
                        text = "Provocările mele",
                        style = AppTypography.titleLarge.copy(fontSize = 22.sp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.Purple,
                            modifier = Modifier.padding(32.dp)
                        )
                    } else {

                        if (uiState.userChallenges.isEmpty()) {

                            Text(
                                text = "Nu ai provocari personalizate.\nAdauga una noua!",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp),
                                color = AppColors.Purple.copy(alpha = 0.6f)
                            )

                        } else {

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp)
                            ) {
                                items(uiState.userChallenges) { (id, challenge) ->
                                    ChallengeItem(
                                        challenge = challenge,
                                        onDelete = {
                                            viewModel.deleteChallenge(id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SecondaryButton("Înapoi") {
                        viewModel.goTo(ChallengeState.MENU)
                    }
                }

                ChallengeState.ADD_CHALLENGE -> {

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

                    ButtonS(
                        text = "Salvează",
                        enabled = newChallenge.isNotBlank()
                    ) {
                        viewModel.addChallenge(newChallenge)
                        newChallenge = ""
                    }

                    SecondaryButton("Înapoi") {
                        viewModel.goTo(ChallengeState.MENU)
                    }
                }
            }
        }
    }

    if (uiState.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            confirmButton = {
                Button(onClick = { viewModel.dismissDialog() }) {
                    Text("OK")
                }
            },
            title = {
                Text(
                    text = "✓ Succes!",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Purple
                )
            },
            text = {
                Text("Provocare adaugata cu succes!")
            }
        )
    }
}

@Composable
fun ChallengeItem(
    challenge: String,
    onDelete: () -> Unit
) {
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

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Șterge",
                    tint = AppColors.Purple
                )
            }
        }
    }
}
