package com.example.myapplication.uii

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.data.UserWordsRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class AuthActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // daca e deja logat, mergi la MainActivity
        if (auth.currentUser != null) {
            goToMainActivity()
            return
        }

        setContent {
            LoginScreen( isLoading = isLoading, onLoginClick = { signInWithGoogle() } )
        }
    }

    private fun signInWithGoogle() {
        isLoading = true  // sa apara cercul ca se incarca

        //configurez optiuni ca sa pot lua inf de la contul google
        val gop = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // un obiect care stie ce vreau sa obitn (gop) si contine printre altele si signInIntent (deschide UI de login) signOut
        val client = GoogleSignIn.getClient(this, gop)

        // Sign out inainte de sign in pt daca a mai fost logat inainte sa il forteze sa aleaga din nou, sa nu il conecteze
        // la acelasi cont, poate vrea altul
        client.signOut().addOnCompleteListener {
            //  signOut() returneaza un Task si Task are metoda add... care spune : ruleaza codul ...
            // folosesc CompleteListener si nu OnSuccessListener pt la inceput poate nu e conectat la vreun cont
            googleSignInLauncher.launch(client.signInIntent)
        }
    }

    // functia regi.. are 2 parametri un obiect si o functie ( care e callback)
    //StartActivityForResult() este un constructor care are <Intent si ActivityResult>   Intent ca intrare si Activ.. ca output  (nu il returneaza ca e constructor)
    // functia de callback ruleaza dupa ce s-a deschis panoul ala, am selectat contul si am revenit in aplicatie
    // result e de tip ActivityResult adica outputul de la constructor
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data) // salvez intr-un task pt poate fi un rezultat sau o eroare

        try {
            val account = task.getResult(ApiException::class.java)
            // accout contine toate datele si verific  daca id.Token e valid, nu e null
            account?.idToken?.let { idToken ->
                firebaseAuthWithGoogle(idToken)
            } ?: run {
                isLoading = false
                Toast.makeText(this, "Nu s-a putut obtine token-ul", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            isLoading = false
            Toast.makeText(this, "Autentificare esuata: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        // trimite credetialele la firebase
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                saveUserToFirestore()
                Toast.makeText(this, "Autentificare reușită!", Toast.LENGTH_SHORT).show()
                // inainte sa mearga in MainActivity asteapta putin pt Firestore sa salveze
                //Handler  e un obiect care poate trimite comenzi catre un thread, Looper e pt mesaje catre un thread
                //Handler(looper) vreau sa trimit mesaj catre thread-ul care are acest looper (  la mn getMainLooper obtin looper-ul principal
                // pt modificare de ferestre se face pe threadu-ul principal)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isLoading = false
                   /* lifecycleScope.launch {
                        UserWordsRepository().initializeDefaultWords()
                    }*/

                    goToMainActivity()
                }, 500)
            }
            .addOnFailureListener { e ->
                isLoading = false
                Toast.makeText(this, "Eroare firebase: ", Toast.LENGTH_LONG).show()
            }
    }


    private fun saveUserToFirestore() {
        val user = auth.currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        val userData = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "name" to user.displayName,
            "photoUrl" to user.photoUrl?.toString(),
            "provider" to "google",
            "lastLogin" to FieldValue.serverTimestamp()
        )

        val userRef = db.collection("users").document(user.uid)

        // verifica dacă userul exista deja
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // daca exista da update doar la lastLogin
                    userRef.update("lastLogin", FieldValue.serverTimestamp())
                } else {
                    userData["createdAt"] = FieldValue.serverTimestamp() // adaug un camp nou pt cand a fost creat contul
                    userRef.set(userData)
                        .addOnSuccessListener {
                            // functia de initializare cuvinte e suspend deci trebuie apelata dintr-o corutina si e suspend ca sa nu blocheze main ca trebuie sa astepte Firestore
                            // daca vrei sa populezi BD la conectarea unui jucator nou decomentez dar eu vreau sa folosesc cuvintele din WordRepository initial
                            /*lifecycleScope.launch {
                                try {
                                    UserWordsRepository().initializeDefaultWords()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }*/

                        }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
fun LoginScreen(isLoading: Boolean, onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Salut!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF6A1B9A))
                Text("Se încarcă...", color = Color(0xFF6A1B9A))
            } else {
                Button(onClick = onLoginClick) {
                    Text("Autentificare cu Google")
                }
            }
        }
    }
}