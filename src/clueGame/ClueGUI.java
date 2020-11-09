package clueGame;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ClueGUI extends JFrame{
	
	GameControlPanel gameControlPanel;

	public ClueGUI() {
		setTitle("Clue Game");
		setSize(750,250);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createLayout();
	}

	private void createLayout() {
		gameControlPanel = new GameControlPanel();
		gameControlPanel.setTurn(new ComputerPlayer( "Col. Mustard", 0, 0, "orange"), 5);
        gameControlPanel.setGuess( "I have no guess!");
        gameControlPanel.setGuessResult( "So you have nothing?");
		add(gameControlPanel, BorderLayout.SOUTH);
	}
	
	public static void main(String[] args) {
		JFrame clueGUI = new ClueGUI();
		clueGUI.setVisible(true);
	}

}
