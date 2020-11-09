package clueGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.io.*;

public class Board {

	public static final int NUM_PLAYERS = 6;
	private int numRows, numColumns;
	private String layoutConfigFile = "", setupConfigFile = "", loggerFile = "";
	private BoardCell [][] grid;
	private Set<BoardCell> targets;
	private Map<Character,Room> roomDictionary = new HashMap<>(), spaceDictionary = new HashMap<>();
	private	Map<String, String> peopleDictionary = new HashMap<>();
	private ArrayList<String[]> tempGrid = new ArrayList<>();
	private static Board theInstance = new Board();
	
	private Solution theAnswer;
	private ArrayList<Player> players;
	private LinkedHashSet<Card> deck;
	private LinkedHashSet<Card> weapons;
	private LinkedHashSet<Card> people;
	private LinkedHashSet<Card> rooms;
	private Iterator<Card> topCard;
	
	
	private Board(){
		super();
	}
	
	public static Board getInstance() {
		return theInstance;
	}

	public void initialize() {
		
		// (Re)initialize variables
		targets = new HashSet<>();
		roomDictionary = new HashMap<>();
		spaceDictionary = new HashMap<>();
		tempGrid = new ArrayList<>();
		deck = new LinkedHashSet<>();
		weapons = new LinkedHashSet<>();
		people = new LinkedHashSet<>();
		rooms = new LinkedHashSet<>();
		players = new ArrayList<>();
		numColumns = 0;
		
		loadConfigFiles();
		grid = new BoardCell[numRows][numColumns];
		BoardCell [] row;
		for(int r=0; r<numRows; r++) { 			// rows
			row = new BoardCell[numColumns];
			for(int c=0; c<numColumns; c++){ 	// columns
				row[c]= setupCell(r,c);
			}
			grid[r]=row;
		}
		setupDoors();
		setAdjLists();
		setupPlayers();
		setupDeck(); 	
		deal();		
	}
	
	// returns TRUE if each of the person, room, and weapon in the accusation are correct; else returns FALSE
	public boolean checkAccusation(String person, String room, String weapon) {
		return ((person.equals(theAnswer.getPerson().getCardName())) && room.equals(theAnswer.getRoom().getCardName()) && weapon.equals(theAnswer.getWeapon().getCardName()));
	}
	
	// returns a card that matches one of the suggested cards, if there are any in the other players' hands, else returns null (to show there was no match)
	public Card handleSuggestion(String person, String room, String weapon, Player player) {
		Card culprit = new Card(person, CardType.PERSON);
		Card deathRoom = new Card(room, CardType.ROOM);
		Card murderWeapon = new Card(weapon, CardType.WEAPON);
		Solution suggestion = new Solution(culprit, deathRoom, murderWeapon);
		int playerTracker = player.getPlayerIndex();	// Tracking int to see which player will attempt to refute the suggestion next
		for(int i = 0; i < NUM_PLAYERS-1; i++) {		// NUM_PLAYERS-1 is used to exclude the player who put up the suggestion
			playerTracker++;
			if (playerTracker >= NUM_PLAYERS) {	// cycles back around to the beginning of players
				playerTracker -= NUM_PLAYERS;
			}
			// Checks if the player has a matching card, and returns a random one if they do
			Card match = players.get(playerTracker).disproveSuggestion(suggestion);
			if (match != null) {
				match.setColor(player.getColor());
				return match;
			}
		}
		return null;
	}
	
	// randomly pick the Solution cards and shuffle the remaining cards in the deck
	public void setupDeck() {
		// Takes each of the card decks, shuffles them, then takes the top card.
		try {
			Card personSolution = shuffle(people).iterator().next();
			Card roomSolution = shuffle(rooms).iterator().next();
			Card weaponSolution = shuffle(weapons).iterator().next();
			
			theAnswer = new Solution(personSolution, roomSolution, weaponSolution);
			// Adds all the sets to one set, removes the solution cards, and shuffles it.
			deck.addAll(people);
			deck.addAll(rooms);
			deck.addAll(weapons);
			
			deck.remove(personSolution);
			deck.remove(roomSolution);
			deck.remove(weaponSolution);
			
			deck = shuffle(deck);
		}catch(NoSuchElementException e){
			System.out.println("No cards available");
		}
	}
	
	public LinkedHashSet<Card> shuffle(LinkedHashSet<Card> unshuffled) {
		// Puts the cards into a new arraylist, then shuffles them.
		List<Card> shuffled = new ArrayList<>(unshuffled);
		Collections.shuffle(shuffled);
		return new LinkedHashSet<>(shuffled);		
	}
	
	// deal the cards to the players (excluding the Solution cards)
	public void deal() {
		// Makes an iterator called topCard, then every time it deals, calls
		// topCard.next(). Does this until three cards are dealt. If it runs 
		// out of cards, it stops
		if(!deck.isEmpty()) {
			topCard = deck.iterator();
			int i = 0;
			while(i<NUM_PLAYERS*3 && topCard.hasNext()) {
				players.get(i%NUM_PLAYERS).updateHand(topCard.next());
				i++;
			}
		}
	}
	
	public void setupPlayers() {
		// get name and color from dictionary
		// set up 1 human player
		
		// set up remaining players as computer players
		// iterate through dictionary, skipping person chosen by human player
		int i = 0;
		for (Map.Entry<String, String> it : peopleDictionary.entrySet()) {
			if(i!=0) {
				Player player = new ComputerPlayer(it.getKey(),0,0,it.getValue());
				player.setPlayerIndex(i);
				players.add(player);
			}
			else {
				Player player = new HumanPlayer(it.getKey(),0,0,it.getValue());
				player.setPlayerIndex(i);
				players.add(player);
			}
			i++;
		}
		
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
		// Looks at the letters in the cell and makes the necessary adjustments
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
					char addInfo = cellInfo.charAt(1);

					if(addInfo == '#') {
						cell.setRoomLabel(true);
						roomDictionary.get(roomKey).setLabelCell(cell);
					}
					else if(addInfo == '*') {
						cell.setRoomCenter(true);
						cell.setRoomName(roomDictionary.get(roomKey).getName());
						roomDictionary.get(roomKey).setCenterCell(cell);
					}
					else if((addInfo+"").matches("[A-Z]")){
						cell.setSecretPassage(addInfo);
						roomDictionary.get(roomKey).addSecretPassage(cell);
					}
					else if("^v<>".contains(""+addInfo)) {
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
		// Reads the map of the board
		try {
			loadSetupConfig();
		}catch (BadConfigFormatException b){
			logFile("Incorrect format for '" + setupConfigFile + "', invalid symbols or invalid length\n");
			System.exit(1);
		}
		try {
			loadLayoutConfig();
		}catch(BadConfigFormatException b) {
			logFile("Incorrect format for '" + layoutConfigFile + "', invalid room or character\n");
			System.exit(1);
		}
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
	
	public void loadSetupConfig() throws BadConfigFormatException{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(setupConfigFile));
			String line = "";
			while (line != null) {
				line = reader.readLine();
				if(line == null)
					break;
				line = line.strip();
				if(line.indexOf("/") < 0 && !line.equals("")) { // Parse if the line is not commented with a '//' AND If the line is not empty
					String[] array = line.split(",");
					for (int i = 0; i<array.length; i++) {	// clean extra whitespace
						array[i] = array[i].strip();
					}
					if(array[0].equalsIgnoreCase("WEAPON")){
						weapons.add(new Card(array[1], CardType.WEAPON));
					}
					else if (array.length != 3) {
						throw new BadConfigFormatException();
					}
					if(array[0].equalsIgnoreCase("PEOPLE")){
						// Add to peopleDictionary
						peopleDictionary.put(array[1],array[2]);
						// Make people card
						people.add(new Card(array[1], CardType.PERSON));
					}
					else if (array[0].equalsIgnoreCase("SPACE")) {
						spaceDictionary.put(array[2].charAt(0), new Room(array[1]));
					} else if (array[0].equalsIgnoreCase("ROOM")) {
						rooms.add(new Card(array[1], CardType.ROOM));
						roomDictionary.put(array[2].charAt(0), new Room(array[1]));
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
	
	public void loadLayoutConfig() throws BadConfigFormatException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(layoutConfigFile));
			String line = "";
			int rowCount = 0;
			while (line != null) {
				line = reader.readLine(); 
				if (line == null)
					break;
				rowCount++;
				// reads in each line in the layout config text file and breaks the line into Strings for processing
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
				adjList = new HashSet<>();
				if(grid[r][c].isRoom()){
					if(grid[r][c].isRoomCenter()){
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
				}
				else {
					// This bit of code sets adjacency lists for hallways. If there is an edge of the board,
					// or a person, or a room, don't add it to the adjacent list.
					if(r!=numRows-1 && !grid[r+1][c].isRoom()&&!grid[r+1][c].getOccupied()) {
						adjList.add(grid[r+1][c]);
					}
					if(r!=0&&!grid[r-1][c].isRoom()&&!grid[r-1][c].getOccupied()) {
						adjList.add(grid[r-1][c]);
					}
					if(c!=numColumns-1&&!grid[r][c+1].isRoom()&&!grid[r][c+1].getOccupied()) {
						adjList.add(grid[r][c+1]);
					}
					if(c!=0 && !grid[r][c-1].isRoom()&&!grid[r][c-1].getOccupied()) {
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
		// This function acts as an entry point to the recursion
		
		targets = new HashSet<>();
		// If we start in a room, get us out and then calculate targets.
		Set<BoardCell> visited = new HashSet<>();
		if(startCell.isRoom()) {
			for(BoardCell c : startCell.getAdjList()) {
				visited.add(startCell);
				if(!c.getOccupied())
					recursivelyCalcTargets(c, pathlength-1, visited);
			}
		}
		else {
			recursivelyCalcTargets(startCell, pathlength, visited);
		}
	}
	
	public void recursivelyCalcTargets(BoardCell startCell, int pathlength, Set<BoardCell> visited) {
		visited.add(startCell);
		// If we wind up in a room, add the target and do nothing else
		// Base case
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
				for(BoardCell c : startCell.getAdjList()) {
					if (!visited.contains(c))
						recursivelyCalcTargets(c, pathlength-1, visited);
				}
			}
		}
		visited.remove(startCell);
		
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
		char roomKey = cell.getInitial();
		if (roomDictionary.containsKey(roomKey)){
			return roomDictionary.get(roomKey);
		} else {
			return spaceDictionary.get(roomKey);
		}
	}
	
	public Set<BoardCell> getAdjList(int row, int col) {
		return grid[row][col].getAdjList();
	}
	
	/*************************************************************************
	 * For Testing
	 *************************************************************************/
	
	public int getPlayerListSize() {
		return players.size();
	}
	
	public Player getPlayer(int index) {
		return players.get(index);
	}
	
	public Map<String, String> getPeopleDict() {
		return peopleDictionary;
	}
	
	public LinkedHashSet<Card> getDeck() {
		return deck;
	}
	
	public LinkedHashSet<Card> getWeapons() {
		return weapons;
	}
	
	public LinkedHashSet<Card> getPeople() {
		return people;
	}
	
	public LinkedHashSet<Card> getRooms() {
		return rooms;
	}
	
	public Solution getSolution() {
		return theAnswer;
	}

	public void setTheAnswer(Solution theAnswer) {
		this.theAnswer = theAnswer;
	}
	
	public BoardCell[][] getGrid(){
		return grid;
	}
	
}
