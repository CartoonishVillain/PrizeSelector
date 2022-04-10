package com.villain;

public class PlayerData {
    String UUID;
    float chance;

    public PlayerData(String string, float entries) {
        UUID = string;
        chance = entries;
    }

    public float getChance() {
        return chance;
    }

    public String getUUID() {
        return UUID;
    }

}
