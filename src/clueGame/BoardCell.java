package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import experiment.TestBoardCell;

public class BoardCell extends JPanel{

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
	private boolean isUnused = false;
	private boolean roomLabel = false;
	private boolean roomCenter = false;
	private char secretPassage = ' ';
	private Set<BoardCell> adjList = new HashSet<>();
	private String roomName = "";	
	private Color color;
	private boolean validTarget = false;

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
	
	public void setAdjList(Set<BoardCell> adjList) {
		this.adjList= adjList;
	}
	
	public String toString() {
		return ("Row: "+row+", Col: "+col);		
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public void setRoomName(String name) {
		roomName = name;
	}
	
	public void draw(Graphics g, int cellLength) {
		int x = col * cellLength;
		int y = row * cellLength;
		if(validTarget) {
			g.setColor(Color.green);
			g.fillRect(x, y, cellLength, cellLength);
			g.setColor(Color.black);
			g.drawRect(x, y, cellLength, cellLength);
		}
		else if(isRoom) {
			if (this.secretPassage == ' ') {
				g.setColor(Color.gray);
				g.fillRect(x, y, cellLength, cellLength);
			} else {
				g.setColor(Color.pink);
				g.fillRect(x, y, cellLength, cellLength);
				Graphics2D g2d = (Graphics2D) g;
				FontMetrics fontMetrics = g2d.getFontMetrics();
				Font font = new Font("Serif", Font.PLAIN, 12);
				g2d.setColor(Color.black);
				g2d.setFont(font);
				g2d.drawString(secretPassage+"", x+(fontMetrics.stringWidth(secretPassage+"")/2), y+cellLength/2+(fontMetrics.stringWidth(secretPassage+""))/2);
			}
		}
		else if(isUnused) {
			g.setColor(Color.black);
			g.fillRect(x, y, cellLength, cellLength);
		}
		else {
			g.setColor(Color.yellow);
			g.fillRect(x, y, cellLength, cellLength);
			g.setColor(Color.black);
			g.drawRect(x, y, cellLength, cellLength);
		}
			
	}

	public void setUnused(boolean b) {
		this.isUnused = b;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}

	public boolean isUnused() {
		return isUnused;
	}
	
	public void setValidTarget(boolean b) {
		this.validTarget = b;
	}
	
	public boolean containsMouse(int clickX, int clickY, int cellLength) {
		int xLoc = col*cellLength, yLoc = row*cellLength;
		Rectangle cell = new Rectangle(xLoc, yLoc, cellLength, cellLength);
		
		if (cell.contains(new Point(clickX, clickY))) {
			return true;
		}
		return false;
	}
	
}
