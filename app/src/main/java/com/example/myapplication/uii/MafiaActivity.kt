package com.example.myapplication.uii

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.game.MafiaGame
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.AppTypography
import com.example.myapplication.ui.theme.ButtonS
import com.example.myapplication.ui.theme.SecondaryButton


class MafiaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MafiaScreen()
        }
    }
}

@Composable
fun MafiaScreen() {

    val game = remember { MafiaGame() }
    val context = LocalContext.current

    var players by remember { mutableIntStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var gameStarted by remember { mutableStateOf(false) }
    var resetKey by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background( Brush.verticalGradient( colors = AppColors.backgroundGradient )),
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

            if (!gameStarted) {
                Text(
                    text = "Mafia",
                    style = AppTypography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { new ->
                        //  doar cifre si maxim 2 caractere
                        if (new.all { it.isDigit() } && new.length <= 2) {
                            inputText = new
                        }
                    },
                    label = { Text("Nr jucători (4-15)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // doar tastatura cu numere
                )


                val isInputValid = inputText.toIntOrNull()?.let { it in 4..15 } == true

                ButtonS(
                    text = "Start",
                    enabled = isInputValid
                ) {
                        val value = inputText.toIntOrNull() ?: return@ButtonS
                        if (value !in 4..15) {
                            Toast.makeText(context, "Numărul de jucători trebuie să fie între 4 și 15", Toast.LENGTH_SHORT).show()
                            return@ButtonS
                        }

                        players = value
                        game.start(players)
                        resetKey++
                        gameStarted = true
                    }

                SecondaryButton("Înapoi") {
                    (context as? ComponentActivity)?.finish()
                }


            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .heightIn(max = 500.dp)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(players) { index ->
                        MafiaCell(
                            resetKey = resetKey,
                            getRole = { game.getRoleForPlayer(index) }
                        )
                    }
                }

                ButtonS("Reset") {
                    game.start(players)
                    resetKey++
                }

            }
        }
    }
}

@Composable
fun MafiaCell(resetKey: Int, getRole: () -> String) {
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
                text = getRole(),
                color = AppColors.White,
                style = AppTypography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}