package experiment;

import java.util.Set;

public class TestBoardCell {
	/*
	 * This class represents one cell in a grid.
	 */
	private int row;
	private int col;
	private boolean isOccupied = false;
	private boolean isRoom = false;

	public TestBoardCell(int row, int col) {
		// TODO 
		super();
		this.row = row;
		this.col = col;
	}
	
	public Set<TestBoardCell> getAdjList() {
		return null;
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
