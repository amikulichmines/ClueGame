package experiment;
import java.lang.reflect.Array;
import java.util.Set;
import java.util.TreeSet;

public class TestBoard {
	/*
	 * This class contains the board (the grid of TestBoardCell objects).
	 */
	
	private Set<Set<TestBoardCell>> board;
	private Set<TestBoardCell> targets;
	
	public TestBoard(int h, int w){
		Set<TestBoardCell> row;
		TestBoardCell cell;
		for(int r=0; r<h; r++) { 		// rows
			row = new TreeSet<TestBoardCell>();
			for(int c=0; c<w; c++){ 	// columns
				cell = new TestBoardCell(r,c); 
				row.add(cell);
			}
			board.add(row);
		}
	}
	
	public void calcTargets(TestBoardCell startCell, int pathlength) {
		
	}
	
	public Set<TestBoardCell> getTargets(){
		return targets;
	}
	public TestBoardCell getCell(int row, int col) {
		return null;
		
	}
}
