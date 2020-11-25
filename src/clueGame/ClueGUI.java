package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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

public class ClueGUI extends JFrame implements ActionListener{
	
	// Tests to test functionality. This should cover all the requirements
	// To test one, set it at true and set all other tests to false.
	// Note. This ONLY works with our 6 players.
	
	// Puts a computer player in a room, has it make suggestion.
	public boolean MAKE_SUGGESTION_COMPUTER = true;
	// Puts human in a room, has it show suggestion
	public boolean MAKE_SUGGESTION_HUMAN = true;
	// Make the computer give a true accusation
	public boolean MAKE_ACCUSATION_COMPUTER_WIN = false;
	// Make computer give false accusation
	public boolean MAKE_ACCUSATION_COMPUTER_LOSE = false;
	// Have the human make an accusation (note: tester must actually give it an accusation.)
	public boolean MAKE_ACCUSATION_HUMAN = false;
	// To make sure more than one person can fit
	public boolean ROOM_DOUBLE_OCCUPANCY = true;


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
		gameControlPanel = new GameControlPanel(clueGUI);
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

	private void computerTurn(int dieRoll) {
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
		if(board.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].isRoomCenter()) {
			Solution proposedSolution = currentPlayer.createSuggestion(board.getGrid());
			Card refutingCard = board.handleSuggestion(proposedSolution.person.getCardName(), proposedSolution.room.getCardName(), proposedSolution.weapon.getCardName(), currentPlayer);
			if(refutingCard == null) {
				currentPlayer.setProposedSolution(proposedSolution);
			}else {
				currentPlayer.updateSeen(refutingCard);
			}
		}
		this.repaint();
	}
	
	private void makeAccusation(Solution solution) {
		if(board.checkAccusation(solution.person.getCardName(), solution.room.getCardName(), solution.weapon.getCardName())) {
			// Display the initial screen
			Object[] options = {"OK"};
			String message = "Player " + currentPlayer.getName() + " has made the correct accusation and won! The solution was:\n Person: "+
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
//		promptForAccusation();
		currentPlayer.resetMoveStatus();
		for (BoardCell target : board.getTargets()) {
			target.setValidTarget(true);
		}
		this.repaint();
		if (board.getGrid()[currentPlayer.getRow()][currentPlayer.getColumn()].isRoomCenter()) {
			promptForSuggestion();
		}
	}
	
	private void promptForSuggestion() {
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
		panel7.add(suggestionSubmitButton);
		suggestionGridPanel.add(panel7);

		JPanel panel8 = new JPanel();
		suggestionCancelButton = new JButton("Cancel");
		panel8.add(suggestionCancelButton);
		suggestionGridPanel.add(panel8);

		suggestionFrame.add(suggestionGridPanel);

		suggestionFrame.setVisible(true);
		
		// Move a player to the center cell of the room that you selected
		board.getPlayerDictionary().get(personBox.getSelectedItem())
		.setRow(board.getRoomDictionary().get(roomBox.getSelectedItem()).getCenterCell().getRow());
		
		board.getPlayerDictionary().get(personBox.getSelectedItem())
		.setColumn(board.getRoomDictionary().get(roomBox.getSelectedItem()).getCenterCell().getCol());

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
		panel7.add(accusationSubmitButton);
		accusationGridPanel.add(panel7);

		JPanel panel8 = new JPanel();
		accusationCancelButton = new JButton("Cancel");
		panel8.add(accusationCancelButton);
		accusationGridPanel.add(panel8);

		accusationFrame.add(accusationGridPanel);

		accusationFrame.setVisible(true);
		
	}

	private static int rollDice() {
		return ThreadLocalRandom.current().nextInt(1, 6);
	}

	public void nextTurn(ClueGUI clueGUI) {
		currentPlayer = players.get(turnNumber%players.size());
		board.setCurrentPlayer(currentPlayer);
		panel = clueGUI.gameControlPanel;
		int dieRoll = rollDice();

		// making sure players occupy their spaces
		for (Player player : players) {
			board.getCell(player.getRow(), player.getColumn()).setOccupied(true);
		}

		board.calcTargets(board.getCell(currentPlayer.getRow(), currentPlayer.getColumn()), dieRoll);
		panel.setTurn(currentPlayer, dieRoll);
		if(currentPlayer instanceof HumanPlayer) {
			humanTurn();
		}
		else {
			computerTurn(dieRoll);
		}
		turnNumber++;
		clueGUI.setVisible(true);

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
		} else if (MAKE_SUGGESTION_HUMAN){
			board.getPlayer(0).setRow(board.getRoom('C').getCenterCell().getRow());
			board.getPlayer(0).setColumn(board.getRoom('C').getCenterCell().getCol());
			currentPlayer=board.getPlayers().get(0);
			humanTurn();
		}else if (MAKE_SUGGESTION_COMPUTER){
			board.getPlayer(1).setRow(board.getRoom('C').getCenterCell().getRow());
			board.getPlayer(1).setColumn(board.getRoom('C').getCenterCell().getCol());
			currentPlayer=board.getPlayers().get(1);
			computerTurn(4);
		}else if (MAKE_ACCUSATION_COMPUTER_WIN) {
			Solution solution = board.getTheAnswer();
			
			
		}
			
	}


	public static void main(String[] args) {
		initializeBoard(); // initializes and sets up
		players=board.getPlayers();
		ClueGUI clueGUI = new ClueGUI(players.get(0));
		clueGUI.tests();
		clueGUI.createLayout(players.get(0), clueGUI);
		showSplashScreen(players.get(0).getName());
		clueGUI.nextTurn(clueGUI);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if (e.getSource()==accusationSubmitButton) {
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
		} else if (e.getSource()==accusationCancelButton) {
			accusationFrame.dispatchEvent(new WindowEvent(accusationFrame, WindowEvent.WINDOW_CLOSING));
		} else if (e.getSource()==suggestionSubmitButton) {
			currentPlayer.updateSeen(board.handleSuggestion(personBox.getSelectedItem().toString(), roomBox.getSelectedItem().toString(), weaponBox.getSelectedItem().toString(), currentPlayer));
		} else if (e.getSource()==suggestionCancelButton) {
			suggestionFrame.dispatchEvent(new WindowEvent(suggestionFrame, WindowEvent.WINDOW_CLOSING));
		}
	}

}
