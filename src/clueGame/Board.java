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

public class Board {

	private int numRows;
	private int numColumns;
	private BoardCell [][] grid;
	private Set<BoardCell> targets;
	private String layoutConfigFile = "";
	private String loggerFile = "";
	private Map<Character, Room> roomMap;
	private static Board theInstance = new Board();
	private String csvFile = "";
	private String txtFile = "";
	private Map<Character,Room> roomDictionary = new HashMap<Character,Room>();
	private Map<Character,Room> spaceDictionary = new HashMap<Character,Room>();
	private ArrayList<String[]> tempGrid = new ArrayList<String[]>();
	private ArrayList<Room> roomList; // = new ArrayList<Room>();
	
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
	}
	
	public BoardCell setupCell(int r, int c){
		char[] doorDirections = {'<','^','>','v'}; 
		BoardCell cell = new BoardCell(r,c);
		String cellInfo = tempGrid.get(r)[c];
		try {
			if (!cellInfo.matches("[A-Z][vA-Z#*<>^]?")) {
				throw new BadConfigFormatException();
			}
			cell.setInitial(cellInfo.charAt(0));
			if(cellInfo.length() > 1) {
				char addInfo = cellInfo.charAt(1);
				if (new String(doorDirections).contains(new String(""+addInfo))) {
					cell.setDoorway(true);
					cell.setDoorDirection(addInfo);
				} 
				if(addInfo == '#') {
					cell.setRoomLabel(true);
					roomDictionary.get(cellInfo.charAt(0)).setLabelCell(cell);
				}
				if(addInfo == '*') {
					cell.setRoomCenter(true);
					roomDictionary.get(cellInfo.charAt(0)).setCenterCell(cell);
}
				if((addInfo+"").matches("[A-Z]")){
					cell.setSecretPassage(addInfo);
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
	
//	public void setupRoomList() {
//		for (Room room : roomDictionary) {
//			
//		}
//	}
	
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
			BufferedReader reader = new BufferedReader(new FileReader(txtFile));
			String line = "";
			while (line != null) {
				line = reader.readLine(); 
				if(line == null)
					break;
//				if(line.charAt(0) != '/' && line.charAt(1) != '/') { // if it's not a blank line
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
			} // error: Incorrect format for 'Room, Cookplace, C', not a valid room or space
	
			reader.close();
		}catch(IOException IO) {
			String message = "Error reading the file '"+txtFile+"'. Aborting!";
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
			
			BufferedReader reader = new BufferedReader(new FileReader(csvFile));
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
			String message = "Error reading the file '"+txtFile+"'. Aborting!";
			logFile(message);
			System.exit(1);
		}
	}
	
	public void setConfigFiles(String csvFile, String txtFile) {
		this.csvFile = csvFile;
		this.txtFile = txtFile;
	}
	
	public void calcTargets(BoardCell startCell, int pathlength) {
		targets = new HashSet<BoardCell>();
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
}
