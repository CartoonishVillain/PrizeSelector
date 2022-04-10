package com.villain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static ArrayList<String> prizes = new ArrayList<>();
    static HashMap<String, Float> pool = new HashMap<>();
    static ArrayList<PlayerData> winners = new ArrayList<>();
    static int mode = -1;

    public static void main(String[] args) {

        //Prompt the user for their preferred operation method.
        modeSelect();

        try {
            //Initialization phase. Populate required data fields. (Prize pool, and the pool of users)
            setUpPrizes();
            setUpPool();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("ioException in input phase");
        }
        //Selection phase. Go through each prize and randomly pick a winner, until there are either no more prizes or participants.
        selectWinners();

        //Output phase. Take the selected data, and convert it to a text file, and post it in logs.
        try {
            writeOutput();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("ioException in output phase");
        }
    }

    public static void modeSelect() {
        System.out.println("Prize Selector V1.1");
        System.out.println("Please select filtering mode");
        System.out.println("0: Lowest full number (Eg: 7.6 entries -> 7 entries)");
        System.out.println("1: Utilize exact numbers (Eg: 7.6 entries -> 7.6 entries)");
        System.out.println("2: Standard rounding: (Eg: 7.4 entries -> 7 entries, 7.5 entries -> 8 entries)");
        Scanner scanner = new Scanner(System.in);
        //Loop until the mode is set properly
        while (mode == -1) {
            String input = scanner.nextLine();
            int value;

            //If the value is invalid, reset the loop.
            try {
                value = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Valid number not detected, please try again! (Integer, no decimal points)");
                continue;
            }

            //If the value is 0-2, assign the value to break from the loop, otherwise continue going through and looping.
            switch (value) {
                case 0:
                case 1:
                case 2:
                    mode = value;
                    break;
                default:
                    System.out.println("Unfortunately, " + value + " is not a valid mode ID. Please try again with a value between 0-2.");
                    break;
            }
        }
    }

    public static void setUpPrizes() throws IOException {
        //Open prizes.txt if present
        FileInputStream in;
        try {
            in = new FileInputStream("prizes.txt");
        } catch (FileNotFoundException e) {
            System.out.println("prizes.txt not found!");
            return;
        }

        //Prepare a line by line reader.
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        //Loop through each line of prizes.txt, and assign them as prizes in order.
        String line;
        while ((line = reader.readLine()) != null) {
            prizes.add(line);
        }
        in.close();
        reader.close();
    }

    public static void setUpPool() throws IOException {
        //Open data.csv if present
        FileInputStream in;
        try {
            in = new FileInputStream("data.csv");
        } catch (FileNotFoundException e) {
            System.out.println("data.csv not found!");
            return;
        }

        //Prepare a line by line reader.
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        //Calibration phase. Read the first line of the csv to get bearings
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find the columns needed to work, and mark them as indices.
        int uuidIndex = -1;
        String[] tokens = line.split(","); //split first line into the columns
        int counter = 0;
        for (String content : tokens) {
            String truecontent = content.replaceAll("[-+^.:,\"]", ""); //some formats do weird stuff with extra characters.
            //"player" refers to a UUID or username based on example data. Code here could be edited to match the file, or the file could be edited to match the code.
            if (truecontent.equalsIgnoreCase("player")) {
                uuidIndex = counter; //if the player column is found, mark its index for future imports
                break;
            }
            counter++;
        }

        int entryIndex = -1; //the general process listed above for the players is followed for the entry count as well.
        counter = 0;
        for (String content : tokens) {
            String truecontent = content.replaceAll("[-+^.:,\"]", "");
            if (truecontent.equalsIgnoreCase("entries")) { //"entries" is again, entered here as a reference to example data.
                entryIndex = counter;
                break;
            }
            counter++;
        }

        if (uuidIndex == -1 || entryIndex == -1) { // if either index is missing, stop.
            System.out.println("CSV Index table does not contain necessary values! Need a player and entries column");
            return;
        }

        while ((line = reader.readLine()) != null) { //Go through each line of the data.csv file, indexes in hand, and add each entry to the map.
            tokens = line.split(",");
            if (!tokens[0].equals("")) {
                float value = Float.parseFloat(tokens[entryIndex]);
                pool.put(tokens[uuidIndex], modeMath(value));
            }
        }

        in.close(); //close input.
        reader.close();
    }

    public static Float modeMath(Float value) {
        switch (mode) {
            case 0:
                return (float) value.intValue(); //Mode 0 returns the int value of the float
            case 1:
                return value; //Mode 1 uses the float as is
            case 2:
                float decimal = value - (float) value.intValue(); //Mode 2 uses the int value, or int value plus one of the float depending on the decimal.
                if (decimal < 0.5f) {
                    return (float) value.intValue();
                } else {
                    return (float) value.intValue() + 1;
                }
            default:
                return 0f;
        }
    }

    public static void selectWinners() {
        Random random = new Random();
        while (pool.size() > 0 && winners.size() < prizes.size()) { //while we still have players in the pool, and we have less winners than prizes...
            float Total = 0;
            for (Map.Entry<String, Float> entry : pool.entrySet()) { //Loop through every player in the pool, and add their entry values together
                Total += entry.getValue();
            }
            float randomval = random.nextFloat() * Total; //Generate a random number between 0 and the combined total of all
            String winner = "";
            float chance = 0f;
            for (Map.Entry<String, Float> entry : pool.entrySet()) { //For every user, subtract the value of their entry.
                randomval -= entry.getValue();
                if (randomval <= 0) { //If after subtraction, the random number is 0 or less, their entry was picked, and they are a winner!
                    winner = entry.getKey();
                    chance = entry.getValue();
                    break;
                }
            }

            winners.add(new PlayerData(winner, chance)); //Create a new player data object (stories their UUID and value) and add it to the list of winners
            pool.remove(winner); //Remove the winner from the pool, as to not continue influencing the drawing with multiple pulls.
        }
    }

    public static void writeOutput() throws IOException {
        //Create a writer for output file.
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
        for (PlayerData winner : winners) { //For each winner, match the index of the winner to the index of the associated prize.
            String WinnerString = prizes.get(index) + ": " + winner.getUUID() + " (Entries: " + winner.getChance() + ")"; //List the prize won, player who won it, and their entry count.
            out.write(WinnerString + "\n");
            System.out.println(WinnerString);
            index++;
        }

        out.close();
    }
}
