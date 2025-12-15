package com.example.myapplication

enum class GameMode {
    FARA_AJUTOR,
    CU_AJUTOR,
    CUVANT_SIMILAR
}
object WordRepository {

    // FARA AJUTOR
    // first = cuvant
    // second = "" (nefolosit)
    val faraAjutor = listOf(
        Pair("pizza", ""),
        Pair("snitel", ""),
        Pair("pepene", ""),
        Pair("hamburger", ""),
        Pair("clatita", ""),
        Pair("doctor", ""),
        Pair("spital", ""),
        Pair("plaja", "")
    )

    // CU AJUTOR
    // first = cuvant normal
    // second = hint pt impostor
    val cuAjutor = listOf(
        Pair("casa", "constructie"),
        Pair("masina", "transport"),
        Pair("telefon", "comunicare"),
        Pair("avion", "zbor"),
        Pair("doctor", "sanatate"),
        Pair("restaurant", "mancare")
    )

    // CUVANT SIMILAR
    // first = cuvant jucatori
    // second = cuvant apropiat pt impostor
    val cuvantSimilar = listOf(
        Pair("lamaie", "limonada"),
        Pair("dormitor", "pat"),
        Pair("mare", "ocean"),
        Pair("munte", "deal")
    )

    fun getWords(mode: GameMode): List<Pair<String, String>> {
        return when (mode) {
            GameMode.FARA_AJUTOR -> faraAjutor
            GameMode.CU_AJUTOR -> cuAjutor
            GameMode.CUVANT_SIMILAR -> cuvantSimilar
        }
    }
}


