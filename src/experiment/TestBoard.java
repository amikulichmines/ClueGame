package experiment;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class TestBoard {
	/*
	 * This class contains the board (the grid of TestBoardCell objects).
	 */
	
	private ArrayList<ArrayList<TestBoardCell>> board = new ArrayList<ArrayList<TestBoardCell>>();
	private Set<TestBoardCell> targets;
	
	public TestBoard(int h, int w){
		ArrayList<TestBoardCell> row;
		TestBoardCell cell;
		for(int r=0; r<h; r++) { 		// rows
			row = new ArrayList<TestBoardCell>();
			for(int c=0; c<w; c++){ 	// columns
				cell = new TestBoardCell(r,c); 
				row.add(cell);
			}
			board.add(row);
		}
	}
	
	
	public void calcTargets(TestBoardCell startCell, int pathlength) {
		targets = new HashSet<TestBoardCell>();
	}
	
	public Set<TestBoardCell> getTargets(){
		return targets;
	}
	public TestBoardCell getCell(int row, int col) {
		return board.get(row).get(col);		
	}
}
