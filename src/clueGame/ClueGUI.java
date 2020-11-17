package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClueGUI extends JFrame{
	
	private static GameControlPanel gameControlPanel;
	private static CardsKnownPanel cardsKnownPanel;
	private static Board board;
	private static GameControlPanel panel;
	private static boolean isFinished= false;
	private static ArrayList<Player> players = new ArrayList<>();
	private static int turnNumber = 0;
	boolean validTurn=false;
	Player currentPlayer = players.get(turnNumber%players.size());;
	

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
		ArrayList<BoardCell>targets = new ArrayList(board.getTargets());
		Collections.shuffle(targets);
		BoardCell target = targets.get(0);
		player.move(target.getCol(), target.getRow());
		add(board, BorderLayout.CENTER);
		this.repaint();
	}
	
	private void humanTurn(ClueGUI clueGUI) {	
		// Just show the available spots, let the board class
		// take care of the movements.
		currentPlayer.resetMoveStatus();
		for (BoardCell target : board.getTargets()) {
			target.setValidTarget(true);
		}
		this.repaint();
	}
	
	private static int rollDice() {
		return ThreadLocalRandom.current().nextInt(1, 6);
	}
	
	public void nextTurn(ClueGUI clueGUI) {
		currentPlayer = players.get(turnNumber%players.size());
		System.out.println("Current player "+currentPlayer.getName() + " location (" 
				+ currentPlayer.getRow() + ", " + currentPlayer.getColumn()+")");
		board.setCurrentPlayer(currentPlayer);
		panel = clueGUI.gameControlPanel;
		int dieRoll = rollDice();
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
	

	public static void main(String[] args) {
		initializeBoard(); // initializes and sets up
		players=board.getPlayers();
		ClueGUI clueGUI = new ClueGUI(players.get(0));
		clueGUI.createLayout(players.get(0), clueGUI);
		showSplashScreen(players.get(0).getName());
		clueGUI.nextTurn(clueGUI);
	}

}
