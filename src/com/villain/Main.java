package com.villain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static ArrayList<String> prizes = new ArrayList<>();
    static HashMap<String, Integer> pool = new HashMap<>();
    static ArrayList<String> winners = new ArrayList<>();

    public static void main(String[] args) throws IOException {
	// write your code here
        setUpPrizes();
        setUpPool();
        selectWinners();
        writeOutput();
    }

    public static void setUpPrizes() throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream("prizes.txt");
        } catch (FileNotFoundException e) {
            System.out.println("data.csv not found!");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String line = "";
        while ((line = reader.readLine()) != null) {
            prizes.add(line);
        }
    }

    public static void setUpPool() throws IOException {
        FileInputStream in = null;

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
            if (truecontent.toLowerCase().equals("player")) {
                uuidIndex = counter; //if the date column is found, mark it's index for future imports
                break;
            }
            counter++;
        }

        int entryIndex = -1; //the general process listed above for the date is followed
        counter = 0;
        for (String content : tokens) {
            String truecontent = content.replaceAll("[-+^.:,\"]", "");
            if (truecontent.toLowerCase().equals("entries")) {
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
            int select = 0;
            String winner = "";
            for (Map.Entry<String, Integer> entry : pool.entrySet()) {
                random -= entry.getValue();
                if (random <= 0) {
                    winner = entry.getKey();
                    break;
                }
            }

            winners.add(winner);
            pool.remove(winner);
        }
    }

    public static void writeOutput() throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter("output.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("output.txt not found!");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int index = 0;
        for (String winner : winners) {
            String WinnerString = prizes.get(index) + ": " + winner;
            out.write(WinnerString + "\n");
            System.out.println(WinnerString);
            index++;
        }

        out.close();
    }
}
