package com.example.myapplication

import java.sql.Types.NULL

enum class GameMode {
    FARA_AJUTOR,
    CU_AJUTOR,
    CUVANT_SIMILAR,
    NUMAR
}
object WordRepository {

    val faraAjutor = listOf(
        "pizza", "cutremur", "Stefan cel Mare"
    )

    val cuAjutor = listOf(
        Pair("pizza", "mâncare"), Pair("supă", "lichid"), Pair("ciorbă", "acru"),
        Pair("salată", "legume"), Pair("măr", "fruct"), Pair("banană", "fruct"),
        Pair("cartof", "legumă"), Pair("morcov", "legumă"),
        Pair("pat", "somn"), Pair("pernă", "cap"), Pair("pătură", "căldură"),

    )

    val cuvantSimilar = listOf(
        Pair("lamaie", "limonada"),
        Pair("dormitor", "pat"),
        Pair("mare", "ocean"),
        Pair("munte", "deal")
    )

    val numar = listOf("1", "2", "3", "55", "17")

    fun getWords(mode : GameMode) : List<String> {
        if(mode== GameMode.FARA_AJUTOR)
        {
            return faraAjutor
        }
        else if(mode== GameMode.NUMAR)
        {
            return numar
        }

        return listOf()
    }
    fun getPair(mode: GameMode): List<Pair<String, String>> {

        if(mode==GameMode.CU_AJUTOR )
        {
            return cuAjutor
        }
        else if (mode==GameMode.CUVANT_SIMILAR)
        {
            return cuvantSimilar
        }
        return listOf()

    }
}


