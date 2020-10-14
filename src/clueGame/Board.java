package clueGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.*;
import java.lang.System.Logger;
import java.util.regex.*;

import clueGame.BoardCell;
import experiment.TestBoardCell;

public class Board {

	private int numRows;
	private int numColumns;
	private BoardCell [][] grid;
	private Set<BoardCell> targets = new HashSet<BoardCell>();
	private String layoutConfigFile = "";
	private String setupConfigFile = "";
	private String loggerFile = "";
	private static Board theInstance = new Board();
	private Map<Character,Room> roomDictionary = new HashMap<Character,Room>();
	private Map<Character,Room> spaceDictionary = new HashMap<Character,Room>();
	private ArrayList<String[]> tempGrid = new ArrayList<String[]>();
	private Board(){
		super();
	}
	public static Board getInstance() {
		return theInstance;
	}

	public void initialize() {
		loadConfigFiles();
		grid = new BoardCell[numRows][numColumns];
		BoardCell [] row;
		BoardCell cell;
		for(int r=0; r<numRows; r++) { 			// rows
			row = new BoardCell[numColumns];
			for(int c=0; c<numColumns; c++){ 	// columns
				row[c]= setupCell(r,c);
			}
			grid[r]=row;
		}
		setupDoors();
		setAdjLists();
	}
	public void setupDoors() {
		for(int r=0; r<numRows; r++) { 			// rows
			for(int c=0; c<numColumns; c++){ 	// columns
				if(grid[r][c].isDoorway()) {
					BoardCell cell = grid[r][c];
					if(cell.getDoorDirection() == DoorDirection.LEFT) {
						roomDictionary.get(grid[r][c-1].getInitial()).addDoor(cell);
					}
					else if(cell.getDoorDirection() == DoorDirection.UP) {
						roomDictionary.get(grid[r-1][c].getInitial()).addDoor(cell);
					}
					else if(cell.getDoorDirection() == DoorDirection.RIGHT) {
						roomDictionary.get(grid[r][c+1].getInitial()).addDoor(cell);
					}
					else if(cell.getDoorDirection() == DoorDirection.DOWN) {
						roomDictionary.get(grid[r+1][c].getInitial()).addDoor(cell);
					}
				}
			}
		}
	}
	public BoardCell setupCell(int r, int c){
		char[] doorDirections = {'<','^','>','v'}; 
		BoardCell cell = new BoardCell(r,c);
		String cellInfo = tempGrid.get(r)[c];
		try {
			if (!cellInfo.matches("[A-Z][vA-Z#*<>^]?")) {
				throw new BadConfigFormatException();
			}
			char roomKey = cellInfo.charAt(0);
			cell.setInitial(roomKey);

			if(roomKey == 'X') {
				cell.setOccupied(true);
			} else {
				// if the roomkey is a key of room dictionary
				if(roomDictionary.containsKey(roomKey))
					cell.setRoom(true);
				if(cellInfo.length() > 1) {
					Room room = roomDictionary.get(roomKey);
					char addInfo = cellInfo.charAt(1);

					if(addInfo == '#') {
						cell.setRoomLabel(true);
						roomDictionary.get(roomKey).setLabelCell(cell);
					}
					else if(addInfo == '*') {
						cell.setRoomCenter(true);
						roomDictionary.get(roomKey).setCenterCell(cell);
					}
					else if((addInfo+"").matches("[A-Z]")){
						cell.setSecretPassage(addInfo);
						roomDictionary.get(roomKey).addSecretPassage(cell);
					}
					else if(addInfo == '<') {
						cell.setDoorway(true);
						cell.setDoorDirection(addInfo);
					}
					else if(addInfo == '^') {
						cell.setDoorway(true);
						cell.setDoorDirection(addInfo);
					}
					else if(addInfo == '>') {
						cell.setDoorway(true);
						cell.setDoorDirection(addInfo);
					}
					else if(addInfo == 'v') {
						cell.setDoorway(true);
						cell.setDoorDirection(addInfo);
					}
				}
			}
		} catch (BadConfigFormatException e) {
			logFile("Incorrect format for '" + cellInfo + "' in row "+r+" and column "+c+".");
		}
		return cell;
	}
	
	public void loadConfigFiles () {
		loadSetupConfig();
		loadLayoutConfig();
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
		try {
			BufferedReader reader = new BufferedReader(new FileReader(setupConfigFile));
			String line = "";
			while (line != null) {
				line = reader.readLine(); 
				if(line == null)
					break;
				if(line.indexOf("/") < 0) {
					try {
						String[] array = line.split(",");
						for (int i = 0; i<array.length; i++) {
							array[i] = array[i].strip();
						}
						if (array.length != 3 || array[2].length() != 1) {
							throw new BadConfigFormatException();
						}
						if (array[0].toUpperCase().equals("SPACE")) {
							spaceDictionary.put(array[2].charAt(0), new Room(array[1]));
						} else if (array[0].toUpperCase().equals("ROOM")) {
							
							roomDictionary.put(array[2].charAt(0), new Room(array[1]));
						} else 
							throw new BadConfigFormatException();
					}catch(BadConfigFormatException nE){
						logFile("Incorrect format for '" + line + "', not a valid room or space\n");
						System.exit(1);
					}
				}
			} 
	
			reader.close();
		}catch(IOException IO) {
			String message = "Error reading the file '"+setupConfigFile+"'. Aborting!";
			logFile(message);
			System.exit(1);
		}
	}
	
	public void logFile(String message) {
		try {
			System.out.println(message);
			PrintWriter out = new PrintWriter(loggerFile);
			out.write(message);
		} catch (FileNotFoundException e) {
			System.out.println("Can't write to 'logfile.txt'");
		}
		
	}
	
	public void loadLayoutConfig() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(layoutConfigFile));
			String line = "";
			int rowCount = 0;
			while (line != null) {
				line = reader.readLine(); 
				if (line == null)
					break;
				rowCount++;
				try {
					String[] array = line.split(",");
					if (numColumns == 0) {
						numColumns = array.length; 
					}
					if (numColumns != array.length) {
						throw new BadConfigFormatException();
					}
					for (int i = 0; i<array.length; i++) {
						array[i] = array[i].strip();
						if (array[i].length() < 1 || array[i].length() > 2) {
							throw new BadConfigFormatException();
						}
					}
					tempGrid.add(array);
				}catch(BadConfigFormatException nE){
					logFile("Incorrect format for '" + line + "', invalid symbols or invalid length\n");
					System.exit(1);
				}
			}
			numRows = rowCount;
	
			reader.close();
			
		}catch(IOException IO) {
			String message = "Error reading the file '"+layoutConfigFile+"'. Aborting!";
			logFile(message);
			System.exit(1);
		}
	}
	
	public void setAdjLists() {
		Set<BoardCell> adjList;
		for(int r=0; r<numRows; r++) { 		// rows
			for(int c=0; c<numColumns; c++){		// columns
				adjList = new HashSet<BoardCell>();
				
				if(grid[r][c].isRoom()){
					char roomKey = grid[r][c].getInitial();
					// add any cells that correspond to the doorways of the room
					for (BoardCell cell : roomDictionary.get(roomKey).getDoors()) {
						adjList.add(cell); 	
					}
					// then cells correspond to the room that is the destination of the secret passage
					for (BoardCell cell : roomDictionary.get(roomKey).getSecretPassages()) {
						Room destination = roomDictionary.get(cell.getSecretPassage());
						adjList.add(destination.getCenterCell());
					}
				}
				else {
					if(r!=numRows-1) {
						if(!grid[r+1][c].isRoom()&&!grid[r+1][c].getOccupied())
							adjList.add(grid[r+1][c]);
					}
					if(r!=0) {
						if(!grid[r-1][c].isRoom()&&!grid[r-1][c].getOccupied())
							adjList.add(grid[r-1][c]);
					}
					if(c!=numColumns-1) {
						if(!grid[r][c+1].isRoom()&&!grid[r][c+1].getOccupied())
							adjList.add(grid[r][c+1]);
					}
					if(c!=0) {
						if(!grid[r][c-1].isRoom()&&!grid[r][c-1].getOccupied())
							adjList.add(grid[r][c-1]);
					}
					// if doorway, find the direction to the room and add its center
					if(grid[r][c].isDoorway()) {
						if(grid[r][c].getDoorDirection() == DoorDirection.UP) {
							adjList.add(roomDictionary.get(grid[r-1][c].getInitial()).getCenterCell());
						}
						if(grid[r][c].getDoorDirection() == DoorDirection.RIGHT) {
							adjList.add(roomDictionary.get(grid[r][c+1].getInitial()).getCenterCell());
						}
						if(grid[r][c].getDoorDirection() == DoorDirection.DOWN) {
							adjList.add(roomDictionary.get(grid[r+1][c].getInitial()).getCenterCell());
						}
						if(grid[r][c].getDoorDirection() == DoorDirection.LEFT) {
							adjList.add(roomDictionary.get(grid[r][c-1].getInitial()).getCenterCell());
						}
					}
				}
				grid[r][c].setAdjList(adjList);			
			}
		}
	}
	
	
	public void setConfigFiles(String csvFile, String txtFile) {
		this.layoutConfigFile = csvFile;
		this.setupConfigFile = txtFile;
	}
	
	public void calcTargets(BoardCell startCell, int pathlength) {
		targets = new HashSet<BoardCell>();
		// If we start in a room, get us out and then calculate targets.
		Set<BoardCell> visited = new HashSet<BoardCell>();
		int branch = 0;
		if(startCell.isRoom()) {
			for(BoardCell c : startCell.getAdjList()) {
				visited.add(startCell);
				if(!c.getOccupied())
					recursivelyCalcTargets(c, pathlength-1, visited, branch);
			}
		}
		else {
			recursivelyCalcTargets(startCell, pathlength, visited, branch);
		}
	}
	
	public void recursivelyCalcTargets(BoardCell startCell, int pathlength, Set<BoardCell> visited, int branch) {
		// If we wind up in a room, add the target and do nothing else
		Set<BoardCell> tempVisited = new HashSet<BoardCell>(visited);
		tempVisited.add(startCell);
		if(startCell.isRoom()) {
			targets.add(startCell);
		}
		else if(!startCell.getOccupied()) {
			if(pathlength == 0) {
				if(!startCell.isRoom()&&!startCell.getOccupied()) {
					targets.add(startCell);
				}
			}
			else {
				branch++;
				for(BoardCell c : startCell.getAdjList()) {
					if (!tempVisited.contains(c))
						recursivelyCalcTargets(c, pathlength-1, tempVisited, branch);
				}
			}
		}
	}
	
	
	public Set<BoardCell> getTargets(){
		return targets;
	}
	public BoardCell getCell(int row, int col) {
		return grid[row][col];		
	}
	
	public Room getRoom(char roomKey) {
		if (roomDictionary.containsKey(roomKey)){
			return roomDictionary.get(roomKey);
		} else {
			return spaceDictionary.get(roomKey);
		}
	}
	
	public Room getRoom(BoardCell cell) {
		return roomDictionary.get(cell.getInitial());
	}
	
	public Set<BoardCell> getAdjList(int row, int col) {
		return grid[row][col].getAdjList();
	}
}
