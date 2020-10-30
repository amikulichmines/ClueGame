package clueGame;

import java.util.HashSet;
import java.util.Set;

public class Room {
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
}
