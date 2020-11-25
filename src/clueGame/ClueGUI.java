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
	private JButton accusationSubmitButton, accusationCancelButton, suggestionSubmitButton;
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

	private void computerTurn(ClueGUI clueGUI, int dieRoll) {
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
		Player player = board.getPlayers().get(turnNumber%players.size());
		ArrayList<BoardCell>targets = new ArrayList<>(board.getTargets());
		Collections.shuffle(targets);
		if(!targets.isEmpty()) {
			BoardCell target = targets.get(0);
			player.move(target.getCol(), target.getRow()); 
		}
		add(board, BorderLayout.CENTER);
		this.repaint();
	}

	private void humanTurn(ClueGUI clueGUI) {	
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
		final JComboBox<String> peopleBox = new JComboBox<>(choicesArray);
		panel4.add(peopleBox);
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
		final JComboBox<String> weaponsBox = new JComboBox<>(choicesArray);
		panel6.add(weaponsBox);
		suggestionGridPanel.add(panel6);

		JPanel panel7 = new JPanel();
		JButton submitButton = new JButton("Submit");
		panel7.add(submitButton);
		suggestionGridPanel.add(panel7);

		JPanel panel8 = new JPanel();
		JButton cancelButton = new JButton("Cancel");
		panel8.add(cancelButton);
		suggestionGridPanel.add(panel8);

		suggestionFrame.add(suggestionGridPanel);

		suggestionFrame.setVisible(true);
		
		// Move a player to the center cell of the room that you selected
		board.getPlayerDictionary().get(peopleBox.getSelectedItem())
		.setRow(board.getRoomDictionary().get(roomBox.getSelectedItem()).getCenterCell().getRow());
		
		board.getPlayerDictionary().get(peopleBox.getSelectedItem())
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
		final JComboBox<String> roomBox = new JComboBox<>(choicesArray);
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
		final JComboBox<String> peopleBox = new JComboBox<>(choicesArray);
		panel4.add(peopleBox);
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
		final JComboBox<String> weaponsBox = new JComboBox<>(choicesArray);
		panel6.add(weaponsBox);
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
			humanTurn(clueGUI);
		}
		else {
			computerTurn(clueGUI, dieRoll);
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
			if (board.checkAccusation(personBox.getSelectedItem().toString(), roomBox.getSelectedItem().toString(), weaponBox.getSelectedItem().toString())) {
				if (currentPlayer instanceof ComputerPlayer) {
					// display message that a computer won
				} else {
					// display human win message
				}
			} else {
				if (currentPlayer instanceof ComputerPlayer) {
					players.remove(currentPlayer);
					board.removePlayer(currentPlayer);
				} else {
					// display human lose message
				}
			}
		} else if (e.getSource()==accusationCancelButton) {
			accusationFrame.dispatchEvent(new WindowEvent(accusationFrame, WindowEvent.WINDOW_CLOSING));
		} else if (e.getSource()==suggestionSubmitButton) {
			
		} else if (e.getSource()==)
	}

}
