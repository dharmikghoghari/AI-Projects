<h1> Search Techniques</h1>

This is a classic <b>N-Queens</b> problem where we have to place <b>N queens</b> on a <b>NxN chess board</b>. The queens should be placed such that none of the queens should hurt the other queen(as in the chess game). <br>

Problem Statement,<br>
We have to find a place to put each baby lizard(queen) in a nursery(chess board). Baby lizards have long tongues and they can hit the other lizards. Our goal is to place them such that they dont hit another baby lizard in the nursery. The baby lizard can shout their tongue up, down, left, right, diagonally as well. Their toungues are very long and can reach to the edge of the nursery.<br>
In addition to the baby lizards, the nursery also has trees i.e. the lizards can not shoot their tongurs through the trees nor they can move into the same place as a tree. The tree blocks other lizards from eating the other lizards. <br>

Input and Output,
Input is an N x N board with the locations of the trees and number of lizards to be placed. The output is simply Yes/No, Yes if lizards can be placed and No if the lizards cannot be placed in the given nursery. <br>

The above problem is solved using 3 Algorithm,<br>
1. <b>Breadth First Search</b>(BFS)<br>
2. <b>Depth First Search</b>(DFS)<br>
3. <b>Simulated Annealing Algorithm</b>(SA)
