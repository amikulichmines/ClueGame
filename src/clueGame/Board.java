package clueGame;

import java.util.ArrayList;
import java.util.Collection;
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
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

public class Board extends JPanel implements MouseListener{

	public static int NUM_PLAYERS = 6;
	private int numRows, numColumns;
	private String layoutConfigFile = "", setupConfigFile = "", loggerFile = "";
	private BoardCell [][] grid;
	private Set<BoardCell> targets;
	private Map<Character,Room> roomDictionary = new HashMap<>(), spaceDictionary = new HashMap<>(); // Key: Room/Space char; value: Room/Space object
	private	Map<String, String> peopleDictionary = new HashMap<>(); // Key: person, value: color
	private Map<String, Player> playerDictionary = new HashMap<>();
	private ArrayList<String[]> tempGrid = new ArrayList<>();
	private Point[] startingPoints = new Point[NUM_PLAYERS];
	
	private Player currentPlayer;
	private ClueGUI clueGUI;
	private static Board theInstance = new Board();
	
	protected Solution theAnswer;
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
		setupStartingPoints();
		setupPlayers();
		setupStartingPoints2();
		setupDeck(); 
		deal();
		
		addMouseListener(this);
	}
	private void setupStartingPoints() {
		for (BoardCell[] cellRow : grid) {					// check through all the cells
			for (BoardCell cell : cellRow) {
				if(!cell.isRoom() && !cell.isUnused()) {	// if cell is valid starting point
					int i;
					for (i = 0; i < NUM_PLAYERS; i++) {		// find first open point in startingPoints
						if (startingPoints[i] == null) {	// and add to startingPoints
							startingPoints[i] = (new Point(cell.getCol(), cell.getRow()));
							break;							// move on to next cell after adding this one
						}
					}
					if (i >= NUM_PLAYERS) {					// if all the slots for starting locations have been filled
						return;								// then exit this function (no need to go through remaining cells)
					}
				}
			}
		}
	}
	
	public void setupStartingPoints2() {
		// Sets a random row and column, if that is a valid place it places them
		// If not, it tries again
		boolean valid;
		int row, col;
		for (Player player : players) {
			valid = false;
			while(!valid) {
				try {
					col = ThreadLocalRandom.current().nextInt(0, numColumns);
					row = ThreadLocalRandom.current().nextInt(0, numRows);
					assertTrue(!grid[row][col].isRoom());
					assertTrue(!grid[row][col].isUnused());
					player.setRow(row);
					player.setColumn(col);
					valid=true;
				} catch(AssertionError e){}	
			}
		}
	}

	// returns TRUE if each of the person, room, and weapon in the accusation are correct; else returns FALSE
	public boolean checkAccusation(String person, String room, String weapon) {
		System.out.println(person+", "+room+", "+weapon);
		System.out.println(theAnswer.getPerson().getCardName()+", "+theAnswer.getRoom().getCardName()+", "+theAnswer.getWeapon().getCardName());
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
				match.setColor(players.get(playerTracker).getColor());
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
				ComputerPlayer player = new ComputerPlayer(it.getKey(),0,0,it.getValue());
				player.setAllCards(people, weapons, rooms);
				player.setPlayerIndex(i);
				players.add(player);
				currentPlayer = player;
			}
			else {
				HumanPlayer player = new HumanPlayer(it.getKey(),0,0,it.getValue());
				player.setAllCards(people, weapons, rooms);
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
				cell.setUnused(true);
				cell.setOccupied(true);
			}
			else {
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
	
	// This function acts as an entry point to the recursion
	public void calcTargets(BoardCell startCell, int pathlength) {
		// Make the currentPlayer's space unoccupied for calculating targets
		grid[currentPlayer.getRow()][currentPlayer.getColumn()].setOccupied(false);
		setAdjLists();
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
		// set currentPlayer's location occupied again
//		grid[currentPlayer.getRow()][currentPlayer.getColumn()].setOccupied(true);
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
	
	@Override
	public void paintComponent(Graphics g) {
		ArrayList<String> playerList = new ArrayList<>();
		super.paintComponent(g);
		int cellLength = this.getWidth()/numColumns;
		for(int r=0; r<numRows; r++) { 		// rows
			for(int c=0; c<numColumns; c++){		// columns
				grid[r][c].draw(g, cellLength);
			}
		}
		Collection<Room> vals = roomDictionary.values();
		for(Room room : roomDictionary.values()) {
			room.draw(g, cellLength);
		}
		for(Player player : players) {
			playerList.add(grid[player.getRow()][player.getColumn()].getRoomName());
		}
		// Draw the players, with logic to make sure you don't draw over anyone 
		// else. This is only applicable to rooms.
		Set<String> drawLocations = new HashSet<>();
		for(Player player : players) {
				int i = 0;
				int[] drawLoc = {player.getRow(), player.getColumn()};
				while(drawLocations.contains(""+drawLoc[0]+drawLoc[1])) {
					i=(i+1)%6;
					int rowAdjust=0;
					int colAdjust = 0;
					if (i==2||i==3||i==4)
						rowAdjust = 1;
					if (i==1||i==2)
						colAdjust=1;
					if(i==4||i==5)
						colAdjust=-1;
					drawLoc[0] = player.getRow() + rowAdjust;
					drawLoc[1] = player.getColumn()+colAdjust;
				}
				drawLocations.add(""+drawLoc[0]+drawLoc[1]);
				player.draw(g, cellLength, drawLoc[0], drawLoc[1]);
		}
	}
	
	ArrayList<String> getPeopleNames() {
		// Gets a randomized arrayList of people
		ArrayList<String> peopleNames = new ArrayList<>();
		for(String person : peopleDictionary.keySet()) {
			peopleNames.add(person);
		}
		Collections.shuffle(peopleNames);
		return peopleNames;
	}
	

	public void setCurrentPlayer(Player player) {
		currentPlayer = player;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	


	@Override
	public void mouseClicked(MouseEvent e) {
		// If current player is ComputerPlayer, do nothing
		if (currentPlayer instanceof ComputerPlayer) {
			return;
		}
		else {
			// Check if mouse is in a proper target
			BoardCell whichCell = null;
			int cellLength = this.getWidth()/numColumns;
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[i].length; j++) {
					// have BoardCell tell if the mouse is in it
					if (grid[i][j].containsMouse(e.getX(), e.getY(), cellLength)) {
						whichCell = grid[i][j];
						break;
					}
				}
				if (whichCell != null) {
					break;
				}
			}
			// if BoardCell is a valid target, perform move
			if (targets.contains(whichCell)) {
				// set current player's location unoccupied (in preparation for move)
				grid[currentPlayer.row][currentPlayer.column].setOccupied(false);
				// move player location
				currentPlayer.move(whichCell.getRow(), whichCell.getCol());
				// set current player's new location occupied
				grid[currentPlayer.row][currentPlayer.column].setOccupied(true);
				// clear valid cells (the player will not be able to select a new cell after clicking a valid cell)
				for (BoardCell cell : targets) {
					cell.setValidTarget(false);
					cell.repaint();
				}
				targets.clear();
				// redraw board
				repaint();
				// if the player entered a room, prompt them for a suggestion
				if (this.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].isRoomCenter()) {
					clueGUI.promptForSuggestion();
				}
			}
			// if BoardCell is not valid target, display error message
			else {
				Object[] options = {"OK"};
				String message = "Not a valid target. Please select a highlighted cell.";
				JOptionPane.showOptionDialog(null, message, "Error",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
						null, options, options[0]);

			}
		}
	}
	
	public Map<String, Player> getPlayerDictionary(){
		for (Player player : players) {
			playerDictionary.put(player.getName(), player);
		}
		return playerDictionary;
	}

	// Override the other abstract methods
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	public void setClueGUI(ClueGUI clueGUI) {
		this.clueGUI = clueGUI;
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
	
	public Solution getTheAnswer() {
		return theAnswer;
	}
	
	public Map<String, Room> getRoomDictionaryByName(){
		Map<String, Room> roomDictionaryByName = new HashMap<>();
		for(Room room : roomDictionary.values()) {
			roomDictionaryByName.put(room.getName(), room);
		}
		return roomDictionaryByName;
	}
	
	public Map<String, Card> getAllRoomCardDictionary(){
		Map<String, Card> allRooms = new HashMap<>();
		for(Card room : rooms) {
			allRooms.put(room.getCardName(), room);
		}
		return allRooms;
	}
	
	public Map<String, Card> getAllPeopleCardDictionary(){
		Map<String, Card> allPeople = new HashMap<>();
		for(Card person : people) {
			allPeople.put(person.getCardName(), person);
		}
		return allPeople;
	}
	
	public Map<String, Card> getAllWeaponCardDictionary(){
		Map<String, Card> allWeapons = new HashMap<>();
		for(Card weapon : weapons) {
			allWeapons.put(weapon.getCardName(), weapon);
		}
		return allWeapons;
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

	public void removePlayer(Player currentPlayer) {
		if (this.currentPlayer.equals(currentPlayer)) {
			players.remove(currentPlayer);
			players.remove(this.currentPlayer);
		}
	}
	
}
