package com.example.myapplication
import kotlin.random.Random

enum class MafiaRole(val displayName: String) {
    IMPOSTOR("IMPOSTOR"),
    MEDIC("MEDIC"),
    POLITIST("POLIȚIST"),
    DENTIST("DENTIST"),
    SATEAN("SĂTEAN")
}

class MafiaGame {

    private val rolesForPlayers = mutableListOf<MafiaRole>()

    fun start(players: Int) {
        rolesForPlayers.clear()

        // BAZA
        rolesForPlayers.add(MafiaRole.IMPOSTOR)
        rolesForPlayers.add(MafiaRole.MEDIC)
        rolesForPlayers.add(MafiaRole.SATEAN)
        rolesForPlayers.add(MafiaRole.SATEAN)

        if (players >= 5) rolesForPlayers.add(MafiaRole.POLITIST)
        if (players >= 6) rolesForPlayers.add(MafiaRole.DENTIST)
        if (players >= 7) rolesForPlayers.add(MafiaRole.SATEAN)
        if (players >= 8) rolesForPlayers.add(MafiaRole.IMPOSTOR)
        if (players >= 9) rolesForPlayers.add(MafiaRole.MEDIC)


        while (rolesForPlayers.size < players) {
            rolesForPlayers.add(MafiaRole.SATEAN)
        }

        rolesForPlayers.shuffle(Random)
    }

    fun getRoleForPlayer(index: Int): String {
        return rolesForPlayers[index - 1].displayName
    }
}
