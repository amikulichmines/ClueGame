package clueGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.*;

import clueGame.BoardCell;

public class Board{

	private ArrayList<ArrayList<BoardCell> > grid = new ArrayList<ArrayList<BoardCell> >();
	private Set<BoardCell> targets;
	private int numRows = 25;
	private int numColumns = 25;
	private String layoutConfigFile = "";
	private Map<Character, Room> roomMap;
	private static Board theInstance = new Board();
	private String csvFile = "";
	private String txtFile = "";
	
	
	private Board(){
		super();
	}
	public static Board getInstance() {
		return theInstance;
	}

	public void initialize() {
		ArrayList<BoardCell> row;
		BoardCell cell;
		for(int r=0; r<numRows; r++) { 		// rows
			row = new ArrayList<BoardCell>();
			for(int c=0; c<numColumns; c++){ 	// columns
				cell = new BoardCell(r,c); 
				row.add(cell);
			}
			grid.add(row);
		}
	}
	
	public void loadConfigFiles () throws BadConfigFormatException {
		
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}
	
	public int getNumColumns() {
		return numColumns;
	}
	
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}
	
	public void loadSetupConfig() {
		
	}
	
	public void loadLayoutConfig() {
		
	}
	
	public void setConfigFiles(String csvFile, String txtFile) {
		
	}
	
	public void calcTargets(BoardCell startCell, int pathlength) {
		targets = new HashSet<BoardCell>();
	}
	
	
	public Set<BoardCell> getTargets(){
		return targets;
	}
	public BoardCell getCell(int row, int col) {
		return grid.get(row).get(col);		
	}
	public Room getRoom(char roomKey) {
		// TODO Auto-generated method stub
		BoardCell cell1 = new BoardCell(1,1), cell2 = new BoardCell(2,2);
		Room temp = new Room("name", cell1, cell2);
		return temp;
	}
	public Room getRoom(BoardCell cell) {
		// TODO Auto-generated method stub
		return null;
	}
}
