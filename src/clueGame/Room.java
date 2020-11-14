package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

public class Room extends JPanel{
	private String name = "";
	private BoardCell centerCell;
	private BoardCell labelCell;
	private Set<BoardCell> doors = new HashSet<>();
	private Set<BoardCell> secretPassages = new HashSet<>();
	
	public Room(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BoardCell getCenterCell() {
		return centerCell;
	}

	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}

	public BoardCell getLabelCell() {
		return labelCell;
	}

	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}

	public Set<BoardCell> getDoors() {
		return doors;
	}

	public void setDoors(HashSet<BoardCell> doors) {
		this.doors = doors;
	}
	
	public void addDoor(BoardCell cell) {
		doors.add(cell);
	}

	public Set<BoardCell> getSecretPassages() {
		return secretPassages;
	}

	public void setSecretPassages(Set<BoardCell> secretPassages) {
		this.secretPassages = secretPassages;
	}
	
	public void addSecretPassage(BoardCell cell) {
		secretPassages.add(cell);
	}
	
	public void draw(Graphics g, int cellLength) {
		Graphics2D g2d = (Graphics2D) g;
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int x = labelCell.getCol() * cellLength;
		int y = labelCell.getRow() * cellLength;
		Font font = new Font("Serif", Font.PLAIN, 12);
		g2d.setColor(Color.black);
		g2d.setFont(font);
		g2d.drawString(name, x-(fontMetrics.stringWidth(name)/2)+cellLength, y);
		drawDoors(g, cellLength);
		
	}
	
	public void drawDoors(Graphics g, int cellLength) {
		g.setColor(Color.red);
		for(BoardCell door:doors) {
			int x = door.getCol() * cellLength;
			int y = door.getRow() * cellLength;
			if(door.getDoorDirection()==DoorDirection.DOWN){
				y+=cellLength;
				g.fillRect(x, y, cellLength, cellLength/10);
			}
			if(door.getDoorDirection()==DoorDirection.UP){
				g.fillRect(x, y, cellLength, cellLength/10);
			}
			if(door.getDoorDirection()==DoorDirection.LEFT){
				g.fillRect(x, y, cellLength/10, cellLength);
			}
			if(door.getDoorDirection()==DoorDirection.RIGHT){
				x+=cellLength;
				g.fillRect(x, y, cellLength/10, cellLength);
			}
		}

	}
}
