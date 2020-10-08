package clueGame;

import java.util.HashSet;
import java.util.Set;

import experiment.TestBoardCell;

public class BoardCell {

	/*
	 * This class represents one cell in a grid.
	 */
	private int row;
	private int col;
	private char initial;
	private DoorDirection doorDirection;
	private boolean isOccupied = false;
	private boolean isRoom = false;
	private boolean roomLabel = false;
	private boolean roomCenter = false;
	private char secretPassage;
	public Set<BoardCell> adjList = new HashSet<BoardCell>();

	public BoardCell(int row, int col) {
		// TODO 
		super();
		this.row = row;
		this.col = col;
	}
	
	public Set<BoardCell> addAdj() {
		return null;
		
	}
	
	public Set<BoardCell> getAdjList() {
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

	public boolean isDoorway() {
		// TODO Auto-generated method stub
		return false;
	}

	public DoorDirection getDoorDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLabel() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRoomCenter() {
		// TODO Auto-generated method stub
		return false;
	}

	public char getSecretPassage() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
