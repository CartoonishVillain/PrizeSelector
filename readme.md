Does all the work of picking your prizes for you, so long as you give it the right data.

What is the right data however?

**Note, items are displayed with an additional newline in between them. This gap should not be present in data, and is only displayed as such because it would not render the fact that they should be new lines otherwise**

prizes.txt: Contains a list of prizes, separated by new lines. Example:

Prize1

Prize2

Prize3

data.csv: A file that contains potential winners and their entries.

example:

player,entries

player1,6.2

player2,2.4

player3,9.1

With the correct data provided, it will pick randomly from the available pool of players to hand prizes out to until either there are no more prizes to give, or players to give them to.
