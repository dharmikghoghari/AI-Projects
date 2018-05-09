<h1> Game Playing Agent: Fruit Rage</h1>

The Fruit Rage is a two player game in which each player tries to maximize his/her share from a batch of fruits randomly placed in a box. The box is divided into cells and each cell is either empty or filled with one fruit of a specific type.<br>

At the beginning of each game, all cells are filled with fruits. Players play in turn and can pick a cell of the box in their own turn and claim all fruit of the same type, in all cells that are connected to the selected cell through horizontal and vertical paths. For each selection or move the agent is rewarded a numeric value which is the square of the number of fruits claimed in that move. Once an agent picks the fruits from the cells, their empty place will be filled with other fruits on top of them (which fall down due to gravity), if any. In this game, no fruit is added during game play. Hence, players play until all fruits have been claimed.<br>

Our goal is to develope an agent that can play against human or another such agent. The agent deveopled here predicts 5 future moves and picks a move that maximizes its chance of winning at the end of the game. The agent is implemented using <b>mini-max</b> and <b>alpha-beta pruning</b> algorithms. <br>

Input and Output,<br>
Input is width and height of the square board (0 < p <= 26), number of fruit types (0 < p <= 9) and the n x n board, with one board row per input file line, and n characters on each line. Each character can be either a digit from 0 to p-1, or a * to denote an empty cell. <br>
Output is the selected move i.e. the fruit you chose and the new board after the selected move has been played. <br>
