package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ClueGUI extends JFrame{
	
	private GameControlPanel gameControlPanel;
	private CardsKnownPanel cardsKnownPanel;
	private static Board board;
	

	public ClueGUI(Player player) {
		setTitle("Clue Game");
		setSize(750,750);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createLayout(player);
	}

	private void createLayout(Player player) {
		gameControlPanel = new GameControlPanel();
		add(gameControlPanel, BorderLayout.SOUTH);
		cardsKnownPanel = new CardsKnownPanel(player);
		add(cardsKnownPanel, BorderLayout.EAST);
	}
	
	public static void main(String[] args) {
		ComputerPlayer player = new ComputerPlayer("Professor Plum", 0, 0, "Red");
		
		// These cards will be in the hand, and their color set to the player's color
		Card revolver = new Card("Revolver", CardType.WEAPON);
		Card rope = new Card("Rope", CardType.WEAPON);
		Card attic = new Card("Attic", CardType.ROOM);
		
		// These cards w
		Card cookplace = new Card("Cookplace", CardType.ROOM);
		cookplace.setColor(Color.yellow);
		Card mustard = new Card("Colonel Mustard", CardType.PERSON);
		mustard.setColor(Color.magenta);
		Card leadPipe = new Card("Lead Pipe", CardType.WEAPON);
		leadPipe.setColor(Color.green);
		
		player.updateHand(attic);
		player.updateHand(revolver);
		player.updateHand(rope);
		player.updateSeen(cookplace);
		player.updateSeen(mustard);
		player.updateSeen(leadPipe);


		
		ClueGUI clueGUI = new ClueGUI(player);
		GameControlPanel panel = clueGUI.gameControlPanel;
		panel.setTurn(new ComputerPlayer( "Col. Mustard", 0, 0, "orange"), 5);
		panel.setGuess( "I have no guess!");
		panel.setGuessResult( "So you have nothing?");
		
		clueGUI.setVisible(true);
	}

}
