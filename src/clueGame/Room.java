package clueGame;

public class Room {
	String name = "";
	BoardCell centerCell;
	BoardCell lableCell;
	
	public Room(String name, BoardCell centerCell, BoardCell lableCell) {
		this.name = name;
		this.centerCell = centerCell;
		this.lableCell = lableCell;
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

	public BoardCell getLableCell() {
		return lableCell;
	}

	public void setLableCell(BoardCell lableCell) {
		this.lableCell = lableCell;
	}

	public BoardCell getLabelCell() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
