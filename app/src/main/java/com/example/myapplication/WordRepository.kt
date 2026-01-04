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
        // =====================
        // 60 – CUVINTE UȘOARE
        // =====================
        "laptop",
        "telefon",
        "televizor",
        "mouse",
        "tastatură",
        "căști",
        "birou",
        "scaun",
        "lampă",
        "priză",
        "șurubelniță",
        "ciocan",
        "clește",
        "mașină",
        "bicicletă",
        "autobuz",
        "tren",
        "avion",
        "ghiozdan",
        "caiet",
        "pix",
        "stilou",
        "tricou",
        "pantaloni",
        "hanorac",
        "șapcă",
        "pantofi",
        "șosete",
        "frigider",
        "aragaz",
        "cuptor",
        "microunde",
        "cană",
        "farfurie",
        "lingură",
        "furculiță",
        "cuțit",
        "masă",
        "dulap",
        "pat",
        "pernă",
        "pătură",
        "baie",
        "duș",
        "oglindă",
        "săpun",
        "prosop",
        "piscină",
        "plajă",
        "minge",
        "bicicletă",
        "rucsac",
        "chei",
        "portofel",
        "ceas",
        "calendar",

        // =====================
        // 20 – CUVINTE CU DUBLU SENS
        // =====================
        "cheie",
        "rețea",
        "cod",
        "bloc",
        "nivel",
        "post",
        "rol",
        "mască",
        "linie",
        "semnal",
        "val",
        "drum",
        "punct",
        "limită",
        "cont",
        "adresă",
        "acces",
        "canal",
        "pistol",
        "bandă",

        // =====================
        // 20 – CUVINTE GRELE (IMPOSTOR MODE)
        // =====================
        "timp",
        "control",
        "libertate",
        "haos",
        "ordine",
        "strategie",
        "identitate",
        "presiune",
        "risc",
        "decizie",
        "secret",
        "adevăr",
        "minciună",
        "atenție",
        "intenție",
        "consecință",
        "echilibru",
        "percepție",
        "influență",
        "responsabilitate"
    )


    val cuAjutor = listOf(
        Pair("palmier", "țări calde"),
        Pair("tricou", "textil"),
        Pair("laptop", "tehnologie"),
        Pair("telefon", "dispozitiv"),
        Pair("bicicletă", "transport"),
        Pair("avion", "călătorie"),
        Pair("tren", "deplasare"),
        Pair("mașină", "vehicul"),
        Pair("ceas", "timp"),
        Pair("calendar", "organizare"),

        Pair("pat", "odihnă"),
        Pair("pernă", "confort"),
        Pair("pătură", "protecție"),
        Pair("canapea", "relaxare"),
        Pair("scaun", "șezut"),

        Pair("pizza", "preparat"),
        Pair("supă", "cald"),
        Pair("salată", "ușor"),
        Pair("măr", "natură"),
        Pair("banană", "galben"),
        Pair("cartof", "pământ"),
        Pair("morcov", "portocaliu"),

        Pair("plajă", "vacanță"),
        Pair("mare", "apă"),
        Pair("munte", "altitudine"),
        Pair("pădure", "verde"),
        Pair("zăpadă", "rece"),

        Pair("oglindă", "reflexie"),
        Pair("ușă", "acces"),
        Pair("fereastră", "deschidere"),
        Pair("cheie", "siguranță"),
        Pair("valiză", "plecare"),

        Pair("școală", "educație"),
        Pair("caiet", "notițe"),
        Pair("pix", "scris"),
        Pair("carte", "informație"),

        Pair("fotbal", "sport"),
        Pair("minge", "joc"),
        Pair("muzică", "sunet"),
        Pair("film", "vizionare"),
        Pair("joc", "distracție"),

        Pair("ploaie", "vreme"),
        Pair("soare", "lumină"),
        Pair("vânt", "mișcare"),
        Pair("noapte", "întuneric"),
        Pair("zi", "lumină")
    )


    val cuvantSimilar = listOf(
        Pair("lămâie", "limonadă"),
        Pair("portocală", "suc"),
        Pair("măr", "fruct"),
        Pair("banană", "galben"),
        Pair("căpșună", "dulce"),

        Pair("mare", "ocean"),
        Pair("lac", "apă"),
        Pair("râu", "curgere"),
        Pair("munte", "deal"),
        Pair("pădure", "copaci"),

        Pair("dormitor", "pat"),
        Pair("bucătărie", "aragaz"),
        Pair("baie", "duș"),
        Pair("living", "canapea"),
        Pair("birou", "calculator"),

        Pair("telefon", "apel"),
        Pair("laptop", "muncă"),
        Pair("televizor", "film"),
        Pair("radio", "muzică"),
        Pair("ceas", "timp"),

        Pair("mașină", "drum"),
        Pair("bicicletă", "pedale"),
        Pair("tren", "șine"),
        Pair("avion", "zbor"),
        Pair("autobuz", "stație"),

        Pair("fotbal", "minge"),
        Pair("tenis", "rachetă"),
        Pair("baschet", "coș"),
        Pair("înot", "apă"),
        Pair("alergare", "viteză"),

        Pair("școală", "elev"),
        Pair("universitate", "student"),
        Pair("spital", "doctor"),
        Pair("restaurant", "mâncare"),
        Pair("magazin", "cumpărături"),

        Pair("film", "actor"),
        Pair("muzică", "sunet"),
        Pair("carte", "pagini"),
        Pair("joc", "distracție"),
        Pair("sport", "mișcare"),

        Pair("noapte", "întuneric"),
        Pair("zi", "lumină"),
        Pair("iarna", "frig"),
        Pair("vara", "căldură"),
        Pair("ploaie", "nori")
    )


    val numar = listOf(
        "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10",

        "11", "12", "13", "14", "15",
        "16", "17", "18", "19", "20",

        "21", "22", "23", "24", "25",
        "27", "29", "31", "33", "37",

        "42", "44", "47", "50", "55",
        "60", "66", "69", "73", "77",

        "88", "90", "99", "100", "111",
        "123", "150", "256", "404"
    )


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


