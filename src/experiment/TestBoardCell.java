package experiment;

import java.util.Set;
import java.util.HashSet;

public class TestBoardCell {
	/*
	 * This class represents one cell in a grid.
	 */
	private int row;
	private int col;
	private boolean isOccupied = false;
	private boolean isRoom = false;
	public Set<TestBoardCell> adjList = new HashSet<TestBoardCell>();

	public TestBoardCell(int row, int col) {
		// TODO 
		super();
		this.row = row;
		this.col = col;
	}
	
	public Set<TestBoardCell> getAdjList() {
		return adjList;
		// TODO returns the adjacency list for the cell
	}
	
	public void setAdjList(Set<TestBoardCell> adjList) {
		this.adjList= adjList;
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
