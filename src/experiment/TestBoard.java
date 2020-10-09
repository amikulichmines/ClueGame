package experiment;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class TestBoard {
	/*
	 * This class contains the board (the grid of TestBoardCell objects).
	 */

	final static int COLS = 4;
	final static int ROWS = 4;
	private TestBoardCell[][] grid = new TestBoardCell[ROWS][COLS];
	private Set<TestBoardCell> targets = new HashSet<TestBoardCell>();
	private Set<TestBoardCell> visited = new HashSet<TestBoardCell>();
	
	public TestBoard(){
		TestBoardCell[] row;
		for(int r=0; r<ROWS; r++) { 		// rows
			row = new TestBoardCell[COLS];
			TestBoardCell cell;
			for(int c=0; c<COLS; c++){ 	// columns
				cell = new TestBoardCell(r,c); 
				row[c]=(cell);
			}
			grid[r] = row;
		}
		setAdjLists();
	}
	
	public void calcTargets(TestBoardCell startCell, int pathlength) {
		recursivelyCalcTargets(startCell, pathlength);
	}
	
	public void recursivelyCalcTargets(TestBoardCell startCell, int pathlength) {
		visited.add(startCell);
		if(pathlength == 0) {
			if(!startCell.isRoom()&&!startCell.getOccupied())
				targets.add(startCell);
		}
		else {
			for(TestBoardCell c : startCell.getAdjList()) {
				if (!visited.contains(c))
					recursivelyCalcTargets(c, pathlength-1);
		
			}
		}
			
	}
	
	public Set<TestBoardCell> getTargets(){
		return targets;
	}
	
	public TestBoardCell getCell(int row, int col) {
		return grid[row][col];		
	}
	
	public void setAdjLists() {
		Set<TestBoardCell> adjList;
		for(int r=0; r<ROWS; r++) { 		// rows
			for(int c=0; c<COLS; c++){		// columns
				adjList = new HashSet<TestBoardCell>();
				if(r!=ROWS-1) {
					if(!grid[r+1][c].isRoom()&&!grid[r+1][c].getOccupied())
						adjList.add(grid[r+1][c]);
				}
				if(r!=0) {
					if(!grid[r-1][c].isRoom()&&!grid[r-1][c].getOccupied())
						adjList.add(grid[r-1][c]);
				}
				if(c!=COLS-1) {
					if(!grid[r][c+1].isRoom()&&!grid[r][c+1].getOccupied())
						adjList.add(grid[r][c+1]);
				}
				if(c!=0) {
					if(!grid[r][c-1].isRoom()&&!grid[r][c-1].getOccupied())
						adjList.add(grid[r][c-1]);
				}
				grid[r][c].setAdjList(adjList);
			}
		}
	}
	
}
