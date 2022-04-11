package com.villain;

import java.io.IOException;

public class PlayerData {
    String UUID;
    float chance;
    String playerName;

    public PlayerData(String string, float entries, boolean connection) {
        UUID = string;
        chance = entries;
        if (connection) { //If connections are enabled, attempt to get the username from the UUID and set it as playername. Otherwise, leave it as an empty string.
            try {
                playerName = NetworkManager.makeNameRequest(UUID);
                playerName += " ";
            } catch (IOException e) {
                System.out.println("Could not get player name for " + UUID + " due to an IOException!");
                playerName = "";
            }
        } else {
            playerName = "";
        }
    }

    public float getChance() {
        return chance;
    }

    public String getUUID() {
        return UUID;
    }

    public String getPlayerName() {
        return playerName;
    }
}
