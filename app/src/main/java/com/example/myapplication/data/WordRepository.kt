package com.example.myapplication.data

enum class GameMode {
    FARA_AJUTOR,
    CU_AJUTOR,
    CUVANT_SIMILAR,
    NUMAR,
    PROVOCARI
}

object WordRepository {

    val faraAjutor = listOf("castravete", "cas", "cs", "st", "ts", "căști", "birou", "scaun", "lampă", "priză")
    val cuAjutor = listOf(Pair("palmier", "țări calde"), Pair("tricou", "textil"), Pair("laptop", "tehnologie"), Pair("telefon", "dispozitiv"))
    val cuvantSimilar = listOf(Pair("emoție", "trăire"), Pair("zâmbet", "bucurie"), Pair("lacrimă", "durere"), Pair("timp", "clipă"))

    val numar = listOf(
        "1","2","3","4","5","6","7","8","9","10",
        "11","12","13","14","15","16","17","18","19","20",
        "21","22","23","24","25","27","29","31","33","37",
        "42","44","47","50","55","60","66","69","73","77",
        "88","90","99","100","111","123","150","256","404",

        "512","666","700","777","808","888","900","999","1000","1010",
        "1111","1212","1313","1414","1515","1616","1717","1818","1919","2000",
        "2024","2025","2222","2345","2468","2500","2600","2700","2800","2900",
        "3000","3333","3500","3600","3700","3800","3900","4000","4040","4321",
        "4444","4500","4567","5000","5050","5120","5432","6000","6969","7000"
    )

    val provocari = listOf(
        "Spune o poveste amuzantă din copilărie",
        "Imită persoana din dreapta ta",
        "Cântă strofa preferată dintr-o melodie",
        "Fă 10 genuflexiuni"
    )


    fun getWords(mode : GameMode) : List<String> {
        return when(mode) {
            GameMode.FARA_AJUTOR -> faraAjutor
            GameMode.NUMAR -> numar
            GameMode.PROVOCARI -> provocari
            else -> listOf()
        }
    }

    fun getPair(mode: GameMode): List<Pair<String, String>> {
        return when(mode) {
            GameMode.CU_AJUTOR -> cuAjutor
            GameMode.CUVANT_SIMILAR -> cuvantSimilar
            else -> listOf()
        }
    }
}