package clueGame;

import java.util.HashSet;
import java.util.Set;

import experiment.TestBoardCell;

public class BoardCell{

	/*
	 * This class represents one cell in a grid.
	 */
	private int row;
	private int col;
	private char initial;
	private DoorDirection doorDirection;
	private boolean doorway = false;
	private boolean isOccupied = false;
	private boolean isRoom = false;
	private boolean roomLabel = false;
	private boolean roomCenter = false;
	private char secretPassage;
	public Set<BoardCell> adjList = new HashSet<BoardCell>();

	public BoardCell(int row, int col) { 
		super();
		this.row = row;
		this.col = col;
	}
	
	public void setInitial(char c) {
		initial = c;
	}
	
	public char getInitial() {
		return initial;
	}
	
	public Set<BoardCell> addAdj() {
		return null;
		
	}
	
	public void setDoorway(boolean doorway) {
		this.doorway = doorway;
	}
	
	public Set<BoardCell> getAdjList() {
		return adjList;
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
		return doorway;
	}

	public DoorDirection getDoorDirection() {
		return doorDirection;
	}
	public void setDoorDirection(char direction) {
		this.doorDirection = getEnumFromValue(direction);
	}
	
	public boolean isRoomLabel() {
		return roomLabel;
	}

	public void setRoomLabel(boolean roomLabel) {
		this.roomLabel = roomLabel;
	}

	public void setRoomCenter(boolean roomCenter) {
		this.roomCenter = roomCenter;
	}

	public DoorDirection getEnumFromValue(char value) {
		if(value == 'v') return DoorDirection.DOWN;
		if(value == '<') return DoorDirection.LEFT;
		if(value == '>') return DoorDirection.RIGHT;
		if(value == '^') return DoorDirection.UP;
		return DoorDirection.NONE;
	}

	public boolean isLabel() {
		return roomLabel;
	}

	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}

	public boolean isRoomCenter() {
		return roomCenter;
	}

	public char getSecretPassage() {
		return secretPassage;
	}
	
	
	
}
