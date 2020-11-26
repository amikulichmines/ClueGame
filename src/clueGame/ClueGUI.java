package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip; 
import javax.sound.sampled.LineUnavailableException; 
import javax.sound.sampled.UnsupportedAudioFileException; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClueGUI extends JFrame /*implements ActionListener*/{
	
	// Tests to test functionality. This should cover all the requirements
	// To test one, set it at true and set all other tests to false.
	// Note. This ONLY works with our 6 players.
	
	public boolean PLAY_MUSIC = false;
	
	// ----------- TESTING ------------
	// Puts a computer player in a room, has it make suggestion.
	public boolean MAKE_SUGGESTION_COMPUTER = false;
	// Puts human in a room, has it show suggestion
	public boolean MAKE_SUGGESTION_HUMAN = false;
	// Make the computer give a true accusation
	public boolean MAKE_ACCUSATION_COMPUTER_WIN = false;
	// Make computer give false accusation
	public boolean MAKE_ACCUSATION_COMPUTER_LOSE = true;
	// Have the human make an accusation (note: tester must actually give it an accusation.)
	public boolean MAKE_ACCUSATION_HUMAN = false;
	// To make sure more than one person can fit
	public boolean ROOM_DOUBLE_OCCUPANCY = false;

	private int theDie;
	private static GameControlPanel gameControlPanel;
	private static CardsKnownPanel cardsKnownPanel;
	private static Board board;
	private static GameControlPanel panel;
	private static boolean isFinished= false;
	private static ArrayList<Player> players = new ArrayList<>();
	private static int turnNumber = 0;
	boolean validTurn=false;
	Player currentPlayer = players.get(turnNumber%players.size());

	private JComboBox<String> personBox, roomBox, weaponBox;
	private JButton accusationSubmitButton, accusationCancelButton, suggestionSubmitButton, suggestionCancelButton;
	private JPanel accusationGridPanel, suggestionGridPanel;
	private JFrame accusationFrame, suggestionFrame;

	public ClueGUI(Player player) {
		setTitle("Clue Game");
		setSize(1050,750);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private static void showTestInstructions(String message) {
		Object[] options = {"OK"};
		JOptionPane.showOptionDialog(null, message, "Welcome to Clue",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
	}

	private static void showSplashScreen(String playerName) {
		// Display the initial screen
		Object[] options = {"OK"};
		String message = "You are "+ playerName+". \nCan you find the solution\n"
				+ "before the Computer players?";
		JOptionPane.showOptionDialog(null, message, "Welcome to Clue",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
	}

	private void createLayout(Player player, ClueGUI clueGUI) {
		// Set up all the panels
		gameControlPanel = new GameControlPanel(clueGUI, "");
		add(gameControlPanel, BorderLayout.SOUTH);
		cardsKnownPanel = new CardsKnownPanel(player);
		add(cardsKnownPanel, BorderLayout.EAST);
		add(board, BorderLayout.CENTER);
	}

	private static void initializeBoard() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}

	private void computerTurn() {
		// update gameControlPanel
		// {handle accusation}
		// move to random available target
		// {handle suggestion}
		// end turn (wait for human to click Next)
		
		for(BoardCell[] row: board.getGrid()) {
			for (BoardCell cell : row) {
				cell.setValidTarget(false);
			}
		}
		currentPlayer = board.getPlayers().get(turnNumber%players.size());
		if(currentPlayer.getProposedSolution() != null)
			makeAccusation(currentPlayer.getProposedSolution());
		ArrayList<BoardCell>targets = new ArrayList<>(board.getTargets());
		Collections.shuffle(targets);
		if(!targets.isEmpty()) {
			BoardCell target = targets.get(0);
			currentPlayer.move(target.getCol(), target.getRow()); 
		}
		add(board, BorderLayout.CENTER);
		if(playerInRoom()) {
			Solution proposedSolution = currentPlayer.createSuggestion(board.getGrid());
			gameControlPanel.setGuess(proposedSolution);
			Card refutingCard = board.handleSuggestion(proposedSolution.person.getCardName(), proposedSolution.room.getCardName(), proposedSolution.weapon.getCardName(), currentPlayer);
			if(refutingCard == null) {
				gameControlPanel.setResult(false);
				currentPlayer.setProposedSolution(proposedSolution);
			}else {
				gameControlPanel.setResult(true);
				currentPlayer.updateSeen(refutingCard);
			}
		}
		this.repaint();
	}
	
	private void makeAccusation(Solution solution) {
		if(board.checkAccusation(solution.person.getCardName(), solution.room.getCardName(), solution.weapon.getCardName())) {
			// Display the initial screen
			Object[] options = {"OK"};
			String message = "Player " + currentPlayer.getName() + " has made the correct accusation and won! The solution was:\nPerson: "+
			solution.person.getCardName() + "\nRoom: "+solution.room.getCardName()+"\nWeapon: "+solution.weapon.getCardName();
			JOptionPane.showOptionDialog(null, message, "Welcome to Clue",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]);
		}else {
			// Display the initial screen
			Object[] options = {"OK"};
			String message = "Oh no! Player " + currentPlayer.getName() + " has made an incorrect accusation and lost!";
			JOptionPane.showOptionDialog(null, message, "Welcome to Clue",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]);
		}
	}

	private void humanTurn() {	
		// Just show the available spots, let the board class
		// take care of the movements.
		currentPlayer.resetMoveStatus();
		for (BoardCell target : board.getTargets()) {
			target.setValidTarget(true);
		}
		if (board.getTargets().isEmpty()) {
			currentPlayer.setHasMoved(true);
		}
		this.repaint();
		if (currentPlayer.hasMoved()==false && currentPlayer.isMovedBySuggestion()==true && playerInRoom()) {
			promptForSuggestion();
		}
	}
	
	
	private boolean playerInRoom() {
		return board.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].isRoomCenter();
	}
	
	void promptForSuggestion() {
		suggestionFrame = new JFrame("Make a suggestion");
		suggestionFrame.setSize(400, 200);
		suggestionGridPanel = new JPanel(new GridLayout(4,2));

		JPanel panel1 = new JPanel();
		JLabel label1 = new JLabel("Room");
		panel1.add(label1);
		suggestionGridPanel.add(panel1);

		JPanel panel2 = new JPanel();
		JLabel currentRoom = new JLabel(board.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].getRoomName());
		panel2.add(currentRoom);
		suggestionGridPanel.add(panel2);

		JPanel panel3 = new JPanel();
		JLabel label2 = new JLabel("Person");
		panel3.add(label2);
		suggestionGridPanel.add(panel3);

		JPanel panel4 = new JPanel();
		// Gather available people as Strings into array
		ArrayList<String> choices = new ArrayList<>(board.getAllPeopleCardDictionary().keySet());
		String [] choicesArray = new String[choices.size()];
		for(int i=0; i<choices.size();i++) {
			choicesArray[i] = choices.get(i);
		}
		personBox = new JComboBox<>(choicesArray);
		panel4.add(personBox);
		suggestionGridPanel.add(panel4);

		JPanel panel5 = new JPanel();
		JLabel label3 = new JLabel("Weapon");
		panel5.add(label3);
		suggestionGridPanel.add(panel5);

		JPanel panel6 = new JPanel();
		choices = new ArrayList<>(board.getAllWeaponCardDictionary().keySet());
		choicesArray = new String[choices.size()];
		for(int i=0; i<choices.size();i++) {
			choicesArray[i] = choices.get(i);
		}
		weaponBox = new JComboBox<>(choicesArray);
		panel6.add(weaponBox);
		suggestionGridPanel.add(panel6);

		JPanel panel7 = new JPanel();
		suggestionSubmitButton = new JButton("Submit");
		suggestionSubmitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String personSuggest = personBox.getSelectedItem().toString();
				String roomSuggest = board.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].getRoomName();
				String weaponSuggest = weaponBox.getSelectedItem().toString();
				// Display suggestion in gameControlPanel
				gameControlPanel.setGuess(personSuggest, roomSuggest, weaponSuggest);
				Card seenCard = board.handleSuggestion(personSuggest, roomSuggest, weaponSuggest, currentPlayer);
				if (seenCard != null) {
					currentPlayer.updateSeen(seenCard);
					remove(cardsKnownPanel);
					cardsKnownPanel = new CardsKnownPanel(currentPlayer);
					add(cardsKnownPanel, BorderLayout.EAST);
					gameControlPanel.setResult(true);
				} else {	// if no card disproved the suggestion
					gameControlPanel.setResult(false);
				}
				suggestionFrame.dispatchEvent(new WindowEvent(suggestionFrame, WindowEvent.WINDOW_CLOSING));
			}
		});
		panel7.add(suggestionSubmitButton);
		suggestionGridPanel.add(panel7);

		JPanel panel8 = new JPanel();
		suggestionCancelButton = new JButton("Cancel");
		suggestionCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suggestionFrame.dispatchEvent(new WindowEvent(suggestionFrame, WindowEvent.WINDOW_CLOSING));
			}
		});
		panel8.add(suggestionCancelButton);
		suggestionGridPanel.add(panel8);

		suggestionFrame.add(suggestionGridPanel);

		// Move a player to the center cell of the room that you selected
		board.getPlayerDictionary()
		.get(personBox.getSelectedItem())
		.setRow(board.getRoomDictionary()
				.get(board.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].getRoomName())
				.getCenterCell().getRow());
		
		board.getPlayerDictionary().get(personBox.getSelectedItem())
		.setColumn(board.getRoomDictionary()
				.get(board.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].getRoomName())
				.getCenterCell().getCol());
		suggestionFrame.setVisible(true);
	}

	public void promptForAccusation() {
		accusationFrame = new JFrame("Make an accusation");
		accusationFrame.setSize(400, 200);
		accusationGridPanel = new JPanel(new GridLayout(4,2));

		JPanel panel1 = new JPanel();
		JLabel label1 = new JLabel("Room");
		panel1.add(label1);
		accusationGridPanel.add(panel1);

		JPanel panel2 = new JPanel();
		// Gather available rooms as Strings into array
		ArrayList<String> choices = new ArrayList<>(board.getAllRoomCardDictionary().keySet());
		String [] choicesArray = new String[choices.size()];
		for(int i=0; i<choices.size();i++) {
			choicesArray[i] = choices.get(i);
		}
		// Select room drop-down box
		roomBox = new JComboBox<>(choicesArray);
		panel2.add(roomBox);
		accusationGridPanel.add(panel2);

		JPanel panel3 = new JPanel();
		JLabel label2 = new JLabel("Person");
		panel3.add(label2);
		accusationGridPanel.add(panel3);

		JPanel panel4 = new JPanel();
		// Gather available people as Strings into array
		choices = new ArrayList<>(board.getAllPeopleCardDictionary().keySet());
		choicesArray = new String[choices.size()];
		for(int i=0; i<choices.size();i++) {
			choicesArray[i] = choices.get(i);
		}
		personBox = new JComboBox<>(choicesArray);
		panel4.add(personBox);
		accusationGridPanel.add(panel4);

		JPanel panel5 = new JPanel();
		JLabel label3 = new JLabel("Weapon");
		panel5.add(label3);
		accusationGridPanel.add(panel5);

		JPanel panel6 = new JPanel();
		choices = new ArrayList<>(board.getAllWeaponCardDictionary().keySet());
		choicesArray = new String[choices.size()];
		for(int i=0; i<choices.size();i++) {
			choicesArray[i] = choices.get(i);
		}
		weaponBox = new JComboBox<>(choicesArray);
		panel6.add(weaponBox);
		accusationGridPanel.add(panel6);

		JPanel panel7 = new JPanel();
		accusationSubmitButton = new JButton("Submit");
		accusationSubmitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accusationSubmitClicked();
			}
		});
		panel7.add(accusationSubmitButton);
		accusationGridPanel.add(panel7);

		JPanel panel8 = new JPanel();
		accusationCancelButton = new JButton("Cancel");
		accusationCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accusationFrame.dispatchEvent(new WindowEvent(accusationFrame, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		panel8.add(accusationCancelButton);
		accusationGridPanel.add(panel8);

		accusationFrame.add(accusationGridPanel);

		accusationFrame.setVisible(true);
		
	}

	private void rollDice() {
		theDie= ThreadLocalRandom.current().nextInt(0, 6) + 1;
	}

	public void nextTurn(ClueGUI clueGUI) {
		gameControlPanel.resetGuessAndResult();
		currentPlayer = players.get(turnNumber%players.size());
		board.setCurrentPlayer(currentPlayer);
		String filename = currentPlayer.getName()+".png";
		remove(gameControlPanel);
		gameControlPanel=new GameControlPanel(clueGUI, filename);
		add(gameControlPanel, BorderLayout.SOUTH);
		panel = clueGUI.gameControlPanel;
		
		rollDice();
		
		// making sure players occupy their spaces
		for (Player player : players) {
			board.getCell(player.getRow(), player.getColumn()).setOccupied(true);
		}

		board.calcTargets(board.getCell(currentPlayer.getRow(), currentPlayer.getColumn()), theDie);
		panel.setTurn(currentPlayer, theDie);
		if(currentPlayer instanceof HumanPlayer) {
			humanTurn();
			clueGUI.setVisible(true);
			if(suggestionFrame!=null && currentPlayer.isMovedBySuggestion()==true && playerInRoom()) {
				suggestionFrame.setVisible(true);
			}
		}
		else {
			computerTurn();
			clueGUI.setVisible(true);
		}
		turnNumber++;
		
		// Stop here until button is clicked
	}

	public void tests() {
		if(ROOM_DOUBLE_OCCUPANCY) {
			board.getPlayer(0).setRow(board.getRoom('C').getCenterCell().getRow());
			board.getPlayer(0).setColumn(board.getRoom('C').getCenterCell().getCol());
			board.getPlayer(1).setRow(board.getRoom('C').getCenterCell().getRow());
			board.getPlayer(1).setColumn(board.getRoom('C').getCenterCell().getCol());
			board.getPlayer(2).setRow(board.getRoom('C').getCenterCell().getRow());
			board.getPlayer(2).setColumn(board.getRoom('C').getCenterCell().getCol());
			board.getPlayer(3).setRow(board.getRoom('A').getCenterCell().getRow());
			board.getPlayer(3).setColumn(board.getRoom('A').getCenterCell().getCol());
			board.getPlayer(4).setRow(board.getRoom('A').getCenterCell().getRow());
			board.getPlayer(4).setColumn(board.getRoom('A').getCenterCell().getCol());
			board.getPlayer(5).setRow(board.getRoom('A').getCenterCell().getRow());
			board.getPlayer(5).setColumn(board.getRoom('A').getCenterCell().getCol());
		} else if (MAKE_ACCUSATION_HUMAN){
			showTestInstructions("Making the accusation: the preset answers in \n"
					+ "'Make Accusation' are the correct answers.");
			board.getPlayer(0).setRow(board.getRoom('C').getCenterCell().getRow());
			board.getPlayer(0).setColumn(board.getRoom('C').getCenterCell().getCol());
			currentPlayer=board.getPlayers().get(0);
			Card personSolution = new Card("Professor Plum", CardType.PERSON);
			Card roomSolution = new Card("Pantry", CardType.ROOM);
			Card weaponSolution = new Card("Candlestick", CardType.WEAPON);
			board.theAnswer = new Solution(personSolution, roomSolution, weaponSolution);
			humanTurn();
		}else if (MAKE_SUGGESTION_COMPUTER){
			board.getPlayer(1).setRow(board.getRoom('C').getCenterCell().getRow());
			board.getPlayer(1).setColumn(board.getRoom('C').getCenterCell().getCol());
			currentPlayer=board.getPlayers().get(1);
			computerTurn();
		}else if (MAKE_ACCUSATION_COMPUTER_WIN) {
			showTestInstructions("Computer accusation win. The window \ncomes up before the game window, fyi");
			board.getPlayer(0).setRow(10);
			board.getPlayer(0).setColumn(10);
			currentPlayer=board.getPlayers().get(1);
			makeAccusation(board.theAnswer);
		}else if (MAKE_ACCUSATION_COMPUTER_LOSE) {
			showTestInstructions("Computer accusation loss. This plugs in a random combo. \n"
					+ "If it wins, you got lucky.");
			board.getPlayer(0).setRow(10);
			board.getPlayer(0).setColumn(10);
			currentPlayer=board.getPlayers().get(1);
			Card personSolution = new Card("Professor Plum", CardType.PERSON);
			Card roomSolution = new Card("Pantry", CardType.ROOM);
			Card weaponSolution = new Card("Candlestick", CardType.WEAPON);
			makeAccusation(new Solution(personSolution, roomSolution, weaponSolution));
		}
			
	}
	
	public static void clueTheme()  { 
		Clip clip; 
		AudioInputStream audioInputStream; 
		String filePath = "ClueTheme.wav"; 
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY); 
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		initializeBoard(); // initializes and sets up
		players=board.getPlayers();
		ClueGUI clueGUI = new ClueGUI(players.get(0));
		board.setClueGUI(clueGUI);
		clueGUI.tests();
		clueGUI.createLayout(players.get(0), clueGUI);
		showSplashScreen(players.get(0).getName());
		if(clueGUI.PLAY_MUSIC)
			clueTheme();
		clueGUI.nextTurn(clueGUI);
	}

	private void accusationSubmitClicked() {
		// if the accusation is the solution
		Object[] options = {"OK"};
		String message = "", message2 = "";
		if (board.checkAccusation(personBox.getSelectedItem().toString(), roomBox.getSelectedItem().toString(), weaponBox.getSelectedItem().toString())) {

			if (currentPlayer instanceof ComputerPlayer) {
				// display message that a computer won
				message = currentPlayer.getName() +" has solved the mystery! Better luck next time.";
				message2 = "Game Over";
			} else {	
				// display human win message
				message = "You solved the mystery! Congratulations!";
				message2 = "Thanks for playing!";
			}
		} else {	// else if the accusation is wrong
			if (currentPlayer instanceof ComputerPlayer) {
				players.remove(currentPlayer);
				board.removePlayer(currentPlayer);
			} else {
				// display human lose message
				message = "Your accusation was wrong!";
				message2 = "Game Over";
			}
		}
		int x = JOptionPane.showOptionDialog(null, message, message2,
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		// quits the game
		if (x==0) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
/*
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// Action events are handled in their own frame
	}*/

}
