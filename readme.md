Does all the work of picking your prizes for you, so long as you give it the right data.

What is the right data however?

prizes.txt: Contains a list of prizes, separated by new lines. Example:

Prize1<br>Prize2<br>Prize3


data.csv: A file that contains potential winners and their entries.

Example:

player,entries<br>player1,6.2<br>player2,2.4<br>player3,9.1

Note: While any string can be used in the player section, UUIDs are preferred.

With the correct data provided, it will pick randomly from the available pool of players to hand prizes out to until either there are no more prizes to give, or players to give them to.

This is handled by adding together all available weights, generating a random number from 0 to the combined total weight, and systematically going through subtracting the total with each entry's weight until the total weight is 0 or lower. Whichever entry last subtracted from the total is the selected winner, and is removed from the pool in subsequent drawings as to not influence future drawings.

Comes in 3 calculation modes. <br>
Mode 0: Lowest Full Number. Takes any float value for entry counts and removes any decimal precision, leaving only their integer forms. (Eg: 7.6 entries -> 7 entries)<br>
Mode 1: Utilize Exact Numbers. Takes any float value for entry counts and uses them as is, no modifications made. (Eg: 7.6 entries -> 7.6 entries)<br>
Mode 2: Standard rounding. Takes any float value and rounds them to the nearest integer (Eg: 7.4 entries -> 7 entries, 7.5 entries -> 8 entries)

Optionally, you can send each player value selected to Mojang servers for translation. Specifically for when Minecraft UUIDs are in use. Adds an extra blurb to each winner successfully translated with their ingame name.

To use:
Run the jar file with the necessary files in the same directory.

Returns: 
Log printouts of your winners as well as an output.txt files carrying the same information for archival/general usage.
