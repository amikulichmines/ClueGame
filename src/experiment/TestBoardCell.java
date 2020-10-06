package experiment;

import java.util.Set;
import java.util.TreeSet;

public class TestBoardCell {
	/*
	 * This class represents one cell in a grid.
	 */
	private int row;
	private int col;
	private boolean isOccupied = false;
	private boolean isRoom = false;
	private Set<TestBoardCell> adjList = new TreeSet<TestBoardCell>();

	public TestBoardCell(int row, int col) {
		// TODO 
		super();
		this.row = row;
		this.col = col;
	}
	
	public Set<TestBoardCell> getAdjList() {
		TestBoardCell cell = new TestBoardCell(2,2);
		adjList.add(cell);
		return adjList;
		// TODO returns the adjacency list for the cell
	}
	
	public void setRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}
	
	public boolean isRoom() {
		return isRoom;
	}
	
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	
	public boolean getOccupied() {
		return isOccupied;
	}

}
