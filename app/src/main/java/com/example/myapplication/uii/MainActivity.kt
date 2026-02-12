package com.example.myapplication.uii

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.AppTypography
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication.ui.theme.MenuButton

class MainActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        setContent {
            MainMenuScreen(
                userName = auth.currentUser?.displayName ?: "User",
                photoUrl = auth.currentUser?.photoUrl?.toString(),
                onSignOut = { signOut() }
            )
        }
    }

    private fun signOut() {
        // sign out Firebase
        auth.signOut()

        // sign out Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleClient = GoogleSignIn.getClient(this, gso)

        googleClient.signOut().addOnCompleteListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}

@Composable
fun MainMenuScreen( userName: String,  photoUrl: String?, onSignOut: () -> Unit)
{
    val context = LocalContext.current

    Box( modifier = Modifier .fillMaxSize()
        .background( Brush.verticalGradient( colors = AppColors.backgroundGradient ))
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = userName,
                    color = AppColors.White,
                    style = AppTypography.labelMedium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Button(
                onClick = onSignOut,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Purple
                )
            ) {
                Text("Sign out", style = AppTypography.labelLarge)
            }
        }


        Column(
            modifier = Modifier
                .width(320.dp)
                .align(Alignment.Center)
                .background(AppColors.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Alege jocul",
                style = AppTypography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )


            MenuButton("Impostor") {
                context.startActivity(Intent(context, ImpostorActivity::class.java))
            }

            MenuButton("Mafia") {
                context.startActivity(Intent(context, MafiaActivity::class.java))
            }

            MenuButton("Provocări") {
                context.startActivity(Intent(context, ProvocariActivity::class.java))
            }

        }
    }
}


