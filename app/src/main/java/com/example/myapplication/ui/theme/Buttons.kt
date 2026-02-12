package com.example.myapplication.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonS(text: String, enabled: Boolean = true, isLoading: Boolean = false, onClick: () -> Unit)
{
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Purple,
            disabledContainerColor = AppColors.DisabledGray
        ),
        onClick = onClick
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = AppColors.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, style = AppTypography.labelLarge)
        }
    }
}


@Composable
fun SecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AppColors.Purple
        )
    ) {
        Text(text, style = AppTypography.labelLarge)
    }
}


@Composable
fun ModeButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
    ) {
        Text(text, style = AppTypography.labelLarge)
    }
}

@Composable
fun MenuButton( text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Purple
        ),
        onClick = onClick
    ) {
        Text(text, style = AppTypography.labelLarge)
    }
}