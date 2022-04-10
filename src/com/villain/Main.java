package com.villain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static ArrayList<String> prizes = new ArrayList<>();
    static HashMap<String, Integer> pool = new HashMap<>();
    static ArrayList<PlayerData> winners = new ArrayList<>();

    public static void main(String[] args) {
	// write your code here
        try {
            setUpPrizes();
            setUpPool();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("ioException in input phase");
        }
        selectWinners();

        try {
            writeOutput();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("ioException in output phase");
        }
    }

    public static void setUpPrizes() throws IOException {
        FileInputStream in;
        try {
            in = new FileInputStream("prizes.txt");
        } catch (FileNotFoundException e) {
            System.out.println("prizes.txt not found!");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String line;
        while ((line = reader.readLine()) != null) {
            prizes.add(line);
        }
        in.close();
    }

    public static void setUpPool() throws IOException {
        FileInputStream in;

        try {
            in = new FileInputStream("data.csv");
        } catch (FileNotFoundException e) {
            System.out.println("data.csv not found!");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get column ids.
        int uuidIndex = -1;
        String[] tokens = line.split(","); //split first line into the columns
        int counter = 0;
        for (String content : tokens) {
            String truecontent = content.replaceAll("[-+^.:,\"]", ""); //some formats do weird stuff with extra characters. This helps resolve the issue.
            if (truecontent.equalsIgnoreCase("player")) {
                uuidIndex = counter; //if the date column is found, mark it's index for future imports
                break;
            }
            counter++;
        }

        int entryIndex = -1; //the general process listed above for the date is followed
        counter = 0;
        for (String content : tokens) {
            String truecontent = content.replaceAll("[-+^.:,\"]", "");
            if (truecontent.equalsIgnoreCase("entries")) {
                entryIndex = counter;
                break;
            }
            counter++;
        }

        if (uuidIndex == -1 || entryIndex == -1) {
            System.out.println("CSV Index table does not contain necessary values! Need a player and entries column");
        }

        while ((line = reader.readLine()) != null) {
            tokens = line.split(",");
            if (!tokens[0].equals("")) {
                float value = Float.parseFloat(tokens[entryIndex]);
                pool.put(tokens[uuidIndex], (int) value);
            }
        }

        in.close();
    }

    public static void selectWinners() {
        while (pool.size() > 0 && winners.size() < prizes.size()) {
            int Total = 0;
            for (Map.Entry<String, Integer> entry : pool.entrySet()) {
                Total += entry.getValue();
            }
            int random = new Random().nextInt(Total);
            String winner = "";
            float chance = 0f;
            for (Map.Entry<String, Integer> entry : pool.entrySet()) {
                random -= entry.getValue();
                if (random <= 0) {
                    winner = entry.getKey();
                    chance = entry.getValue();
                    break;
                }
            }

            winners.add(new PlayerData(winner, chance));
            pool.remove(winner);
        }
    }

    public static void writeOutput() throws IOException {
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter("output.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("output.txt not found! Considering this file is to be made, how did you even do this?");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO Exception on output");
            return;
        }

        int index = 0;
        for (PlayerData winner : winners) {
            String WinnerString = prizes.get(index) + ": " + winner.getUUID() + " (Entries: " + winner.getChance() + ")";
            out.write(WinnerString + "\n");
            System.out.println(WinnerString);
            index++;
        }

        out.close();
    }
}
