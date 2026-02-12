package com.example.myapplication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserWord(
    val word: String = "",
    val hint: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class UserWordsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    suspend fun addWord(mode: GameMode, word: String, hint: String = ""): Boolean {
        val userId = getUserId() ?: return false

        try {
            val userWord = UserWord(word, hint)

            db.collection("users")
                .document(userId)
                .collection("words")
                .document(mode.name)
                .collection("items")
                .add(userWord)
                .await()

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun deleteWord(mode: GameMode, documentId: String): Boolean {
        val userId = getUserId() ?: return false

        try {
            db.collection("users")
                .document(userId)
                .collection("words")
                .document(mode.name)
                .collection("items")
                .document(documentId)
                .delete()
                .await()

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun getWords(mode: GameMode): List<Pair<String, String>> {
        val userId = getUserId() ?: return emptyList()

        try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("words")
                .document(mode.name)
                .collection("items")
                .get()
                .await()

            return snapshot.documents.mapNotNull { doc ->
                val word = doc.getString("word") ?: return@mapNotNull null
                val id = doc.id
                Pair(id, word)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    suspend fun getWordsWithHint(mode: GameMode): List<Triple<String, String, String>> {
        val userId = getUserId() ?: return emptyList()

        try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("words")
                .document(mode.name)
                .collection("items")
                .get()
                .await()

            return snapshot.documents.mapNotNull { doc ->
                val word = doc.getString("word") ?: return@mapNotNull null
                val hint = doc.getString("hint") ?: ""
                val id = doc.id
                Triple(id, word, hint)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }



    /*



    // pt un utilizator nou populează tabela
    // pot merge pe ideea ca daca e nou si nu vrea sa adauge cuv sa le foloseasca pe cele in WordRepository ca sa nu populez BD
    suspend fun initializeDefaultWords() {
        val userId = getUserId() ?: return

        // Verifică dacă utilizatorul are deja cuvinte
        for ((mode, defaultWords) in defaultWordsByMode) {

            val existing = db.collection("users")
                .document(userId)
                .collection("words")
                .document(mode.name)
                .collection("items")
                .limit(1)
                .get()
                .await()

            if (existing.isEmpty) {
                defaultWords.forEach { word ->
                    db.collection("users")
                        .document(userId)
                        .collection("words")
                        .document(mode.name)
                        .collection("items")
                        .add(word)
                        .await()
                }
            }
        }
    }

    private val defaultWordsByMode = mapOf(
        GameMode.FARA_AJUTOR to listOf(
            UserWord("laptop"),
            UserWord("telefon"),
            UserWord("televizor"),
            UserWord("mouse"),
            UserWord("tastatură")
        ),

        GameMode.CU_AJUTOR to listOf(
            UserWord("Paris", "oraș"),
            UserWord("Măr", "fruct"),
            UserWord("Câine", "animal"),
            UserWord("Masă", "mobilă")
        ),

        GameMode.CUVANT_SIMILAR to listOf(
            UserWord("fericit", "bucuros"),
            UserWord("rapid", "iute"),
            UserWord("mare", "imens")
        ),

        GameMode.PROVOCARI to listOf(
            UserWord("Spune o poveste amuzantă din copilărie"),
            UserWord("Imită persoana din dreapta ta"),
            UserWord("Cântă strofa preferată dintr-o melodie"),
            UserWord("Fă 10 genuflexiuni"),
            UserWord("Spune 3 lucruri pentru care ești recunoscător astăzi")
        )
    )


     */
}