package clueGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.*;
import java.lang.System.Logger;

import clueGame.BoardCell;

public class Board{

	private int numRows = 25;
	private int numColumns = 25;
	private BoardCell [][] grid = new BoardCell[numRows][numColumns];
	private Set<BoardCell> targets;
	private String layoutConfigFile = "";
	private String loggerFile = "";
	private Map<Character, Room> roomMap;
	private static Board theInstance = new Board();
	private String csvFile = "";
	private String txtFile = "";
	private Map<Character,String> roomDictionary = new HashMap<Character,String>();
	private Map<Character,String> spaceDictionary = new HashMap<Character,String>();
	private Board(){
		super();
	}
	public static Board getInstance() {
		return theInstance;
	}

	public void initialize() {
		BoardCell [] row;
		BoardCell cell;
		for(int r=0; r<numRows; r++) { 		// rows
			row = new BoardCell[numColumns];
			for(int c=0; c<numColumns; c++){ 	// columns
				cell = new BoardCell(r,c); 
				row[c]=cell;
			}
			grid[r]=row;
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
		try {
			BufferedReader reader = new BufferedReader(new FileReader(txtFile));
			String line = "";
			while (line != null) {
				line = reader.readLine(); 
				if(line.charAt(0) != '/' && line.charAt(1) != '/') { // if it's not a blank line
					try {
						String[] array = line.split(",");
						for (String s : array) {
							s = s.strip();
						}
						if (array.length != 3 || array[3].length() != 1) {
							throw new BadConfigFormatException();
						}
						if (array[0].toUpperCase() == "SPACE") {
							spaceDictionary.put(array[2].charAt(0), array[1]);
						} else if (array[0].toUpperCase() == "ROOM") {
							roomDictionary.put(array[2].charAt(0), array[1]);
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
