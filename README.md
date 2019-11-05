# AI-Sudoku-Solver
Sudoku Solver using AI concepts such as HEURISTICS and CONSTRAINT SATISFACTION.

Given a particular state of the board, it verifies whether the sudoku can be solved with a given state.
The input is taken in the form of CSV file to the Sudoku board.
Firstly, I assigned all the values to the tiles using the input and removed these values from domains of the other cells according to constraint satisfaction. My algorithm checks if anymore values can be assigned or if domain can further be reduced using following constraints:
1.	If in any particular unit(row, column or cell), only one cell has a particular numeric value in its domain, then this cell is assigned that value. 
2.	If there exists two cells in a particular unit two cells have same pair of values in their domain, then those values are removed from the domain of all the other cells in that unit.
3.	If any cell has single value in its domain, then that cell is assigned the value and it is removed from domains of all the other cells in its unit(row, column or grid).

After problem cannot be further reduced by these constraints, I have applied backtracking using different heuristics. They are given below:
•	cell_heuristic_MCV()- This is a heuristic function that selects the best cell i.e. the one with the smallest domain to assign value.
•	value_heuristic_LCV()- This is a heuristic function that selects the best value(least constrained value, that leaves maximum flexibility for other cells) from the domain of the cell returned by the above method to be assigned to it.

